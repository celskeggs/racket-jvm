#lang racket

(require racket/fixnum)
(require racket/generic)

(require "classdefs.rkt")
(require "descriptors.rkt")
(require "instructions.rkt")

(provide bytecode-conversion)

(struct stack-linked (instruction inputs outputs) #:inspector #f)

(define-generics instr
  (get-nexts instr)
  (stack-proc instr indepth resolvestack) ; (values newdepth newinstruction)
  (get-resolve instr constant-pools)
  (get-name instr))

(struct base/stop () #:inspector #f)
(struct base/advance (next) #:inspector #f)

(define-syntax basic
  (syntax-rules ()
    [(basic name fields ... #:args acountr #:rets rcountr #:base (basein base basecode) #:resolve func)
     (struct name base (fields ...) #:inspector #f
       #:methods gen:instr
       [(define (get-name instr)
          'name)
        (define get-resolve func)
        (define (get-nexts basein)
          basecode)
        (define (stack-proc instr indepth resolvestack)
          (let ((acounti acountr) (rcounti rcountr))
            (let ((acount (if (number? acounti) acounti (acounti instr)))
                  (rcount (if (number? rcounti) rcounti (rcounti instr))))
              (unless (>= indepth acount)
                (error "not enough stack to instruction:" instr))
              (values (+ (- indepth acount) rcount) (stack-linked instr
                                                                  (map resolvestack (range (- indepth acount) indepth)) ; in
                                                                  (map resolvestack (range (- indepth acount) (+ (- indepth acount) rcount))) ; out
                                                                  )))))])]
    [(basic name args ... #:args acount #:rets rcount #:base base)
     (basic name args ... #:args acount #:rets rcount #:base base #:resolve (lambda (i cp) i))]
    [(basic name args ... #:args acount #:rets rcount #:resolve func)
     (basic name args ... #:args acount #:rets rcount #:base (x base/advance (list (base/advance-next x))) #:resolve func)]
    [(basic name args ... #:args acount #:rets rcount)
     (basic name args ... #:args acount #:rets rcount #:base (x base/advance (list (base/advance-next x))))]))
(basic t/nop #:args 0 #:rets 0)
(basic t/ldc cref #:args 0 #:rets 1
       #:resolve (lambda (i cp) (struct-copy t/ldc i
                                             [cref (list-ref cp (t/ldc-cref i))])))
(basic t/aload var #:args 0 #:rets 1)
(basic t/return #:args 0 #:rets 0 #:base (x base/stop empty))
(basic t/getstatic fref #:args 0 #:rets 1
       #:resolve (lambda (i cp) (struct-copy t/getstatic i
                                             [fref (list-ref cp (t/getstatic-fref i))])))
(basic t/invokevirtual mref
       #:args (lambda (i) (+ 1 (length (first (convert-method-descriptor (third (t/invokevirtual-mref i)))))))
       #:rets (lambda (i) (if (eq? jvoid (second (convert-method-descriptor (third (t/invokevirtual-mref i)))))
                              0 1))
       #:resolve (lambda (i cp) (struct-copy t/invokevirtual i
                                             [mref (list-ref cp (t/invokevirtual-mref i))])))
(basic t/invokespecial mref
       #:args (lambda (i) (+ 1 (length (first (convert-method-descriptor (third (t/invokespecial-mref i)))))))
       #:rets (lambda (i) (if (eq? jvoid (second (convert-method-descriptor (third (t/invokespecial-mref i)))))
                              0 1))
       #:resolve (lambda (i cp) (struct-copy t/invokespecial i
                                             [mref (list-ref cp (t/invokespecial-mref i))])))

(define (parse-bytecode bytecode i)
  (let ((L1 (+ i 1))
        (L2 (+ i 2))
        (L3 (+ i 3)))
    (define (u1 x)
      (bytes-ref bytecode x))
    (define (u2 x)
      (fxior (fxlshift (u1 x) 8) (u1 (+ 1 x))))
    (let ((op (u1 i)))
      (case op
        ((00) (t/nop L1))
        ((18) (t/ldc L2 (u1 L1)))
        ((25) (t/aload L2 (u1 L1)))
        ((42 43 44 45) (t/aload L1 (- op 42)))
        ((177) (t/return))
        ((178) (t/getstatic L3 (u2 L1)))
        ((182) (t/invokevirtual L3 (u2 L1)))
        ((183) (t/invokespecial L3 (u2 L1)))
        (else (error "instruction not handled:" op))))))

(define (add-sorted lst x)
  (cond ((empty? lst) (list x))
        ((< x (car lst)) (cons x lst))
        ((= x (car lst)) lst)
        ((> x (car lst)) (cons (car lst) (add-sorted (cdr lst) x)))))
(define (add-sorted-all lst xes)
  (if (empty? xes)
      lst
      (add-sorted-all (add-sorted lst (car xes)) (cdr xes))))
(define (first-not-in-sorted lookin avoid)
  (cond ((empty? lookin) (error "not found"))
        ((empty? avoid) (car lookin))
        ((< (car lookin) (car avoid)) (car lookin))
        ((= (car lookin) (car avoid)) (first-not-in-sorted (cdr lookin) (cdr avoid)))
        ((> (car lookin) (car avoid)) (first-not-in-sorted lookin (cdr avoid)))))

(define (bytecode-conversion max-stack max-locals bytecode constant-pool)
  (generate-real-instructions (destackify max-stack max-locals (strandify (map (curry do-resolution constant-pool) (parse-instructions bytecode))))))

(define (parse-instructions bytecode)
  (sort (let iter ((needed '(0)) (done '()) (resmap '()))
          (if (equal? needed done)
              resmap
              (let* ((handling (first-not-in-sorted needed done))
                     (istr (parse-bytecode bytecode handling)))
                (iter (add-sorted-all needed (get-nexts istr))
                      (add-sorted done handling)
                      (cons (list handling istr) resmap)))))
        (lambda (a b) (< (car a) (car b)))))

(define (do-resolution constant-pool instruction-pair)
  (list (first instruction-pair)
        (get-resolve (second instruction-pair) constant-pool)))

; returns (values (list instruction ...) (list needed-strands))
(define (collect-strand instrs start-at)
  (define (get-instr id)
    (second (assoc id instrs)))
  (let* ((ist (get-instr start-at))
         (refs (sort (get-nexts ist) <))
         (prerefs (filter (curry >= start-at) refs))
         (postrefs (filter (curry < start-at) refs)))
    (if (empty? postrefs)
        (values (list start-at ist) prerefs)
        (let-values ([(old-strand needed) (collect-strand instrs (car postrefs))])
          (values (list* start-at ist old-strand) (add-sorted-all needed (append prerefs (cdr postrefs))))))))
(define (strandify instrs)
  (sort (let iter ((needed '(0)) (done '()) (resmap '()))
          (if (equal? needed done)
              resmap
              (let ((handling (first-not-in-sorted needed done)))
                (let-values ([(strand new-needed) (collect-strand instrs handling)])
                  (iter (add-sorted-all needed new-needed)
                        (add-sorted done handling)
                        (cons strand resmap))))))
        (lambda (a b) (< (car a) (car b)))))

(define (destackify max-stack max-locals strands) ; incomplete
  (define (remapper is-local? index)
    (if is-local?
        (if (and (<= 0 index) (< index max-locals))
            index
            (error "local variable out-of-range:" index))
        (if (and (<= 0 index) (< index max-stack))
            (+ max-locals index)
            (error "stack index out-of-range:" index))))
  (let ((stack-heights (for/list ((strand strands)) (list (first strand) (box (void)))))
        (new-strands (for/list ((strand strands)) (list (first strand) (box (void))))))
    (unless (= 0 (caar strands))
      (error "first strand must be strand 0!"))
    
    (define (calc-strand-iterate strand height)
      (if (empty? strand)
          empty
          (let* ((ist (car strand))
                 (next-id (if (empty? (cdr strand)) (void) (cadr strand)))
                 (rest (if (empty? (cdr strand)) empty (cddr strand)))
                 (links (get-nexts ist)))
            (let-values ([(new-height newinstruction) (stack-proc ist height (curry remapper #f))])
              (when (negative? new-height)
                (error "stack underflow on" ist new-height))
              (for ((link links)
                    #:unless (equal? link next-id))
                (let ((h (unbox (second (assoc link stack-heights)))))
                  (unless (or (void? h) (= h new-height))
                    (error "conflicting heights at:" link h new-height))
                  (calc-strand-stack (second (assoc link strands)) new-height)))
              (when (empty? links)
                (unless (= new-height 0)
                  (error "stack not empty at strand termination:" new-height)))
              (cons newinstruction
                    (calc-strand-iterate rest new-height))))))
    
    (define (calc-strand-stack strand begin-height)
      (let* ((id (car strand))
             (code (cdr strand))
             (sbox (second (assoc id stack-heights))))
        (unless (or (void? (unbox sbox)) (= (unbox sbox) begin-height))
          (error "mismatched stack height at strand beginning"))
        (set-box! sbox begin-height)
        (set-box! (second (assoc id new-strands))
                  (calc-strand-iterate code begin-height))))
    
    (calc-strand-stack (car strands) 0)
    (for ((height stack-heights))
      (when (void? (unbox (second height)))
        (error "stack-height is still void")))
    (for/list ((strand new-strands))
      (cons (unbox (second (assoc (car strand) stack-heights))) (unbox (second strand))))))

(define (generate-real-instructions strands)
  (map generate-for-strand strands))
(define (generate-for-strand strand)
  (cons (car strand) (append* (map generate-for-instruction (cdr strand)))))
(define (generate-for-instruction instr)
  (let ((i-real (stack-linked-instruction instr)) (args (stack-linked-inputs instr)) (rets (stack-linked-outputs instr)))
    (case (get-name i-real)
      ((t/aload) (list (i/mov (t/aload-var i-real) (car rets))))
      ((t/invokespecial)
       (let ((mref (t/invokespecial-mref i-real)))
         (list (i/call (if (equal? (second mref) "<init>")
                           mref (cons '#:special-lookup mref))
                       args (if (empty? rets) -1 (car rets))))))
      ((t/invokevirtual)
       (let ((mref (t/invokevirtual-mref i-real)))
         (list (i/const-int -1 (first mref))
               (i/const-int -2 (cdr mref))
               (i/call (list 'vm/VMDispatch "resolveVirtual" "(II)I") (list -1 -2) -1)
               (i/dyncall -1 args (if (empty? rets) -1 (car rets))))))
      ((t/return) (list (i/const-int -1 0)
                        (i/return -1)))
      ((t/getstatic)
       (let* ((fref (t/getstatic-fref i-real))
              (fdesc (convert-field-descriptor (third fref))))
         (list (i/get-static (car rets) fref (get-bytelen-for-type fdesc)))))
      ((t/ldc)
       (let ((value (t/ldc-cref i-real)))
         (if (string? value)
             (list (i/const-int -1 value)
                   (i/call (list 'vm/VMDispatch "getStringByID" "(I)Ljava/lang/String;") (list -1) (car rets)))
             (if (symbol? value)
                 (list (i/const-int -1 value)
                       (i/call (list 'vm/VMDispatch "getClassByID" "(I)Ljava/lang/Class;") (list -1) (car rets)))
                 (list (i/const-int (car rets) value))))))
      (else (error "unhandled front-end instruction:" instr)))))