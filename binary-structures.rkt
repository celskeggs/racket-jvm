#lang racket

(require (for-syntax racket))
(require racket/fixnum)

(provide binstruct u1 u2 u4 u8 array load-file parse-flags)

(define-for-syntax (get-as-string a)
  (if (syntax? a)
      (get-as-string (syntax-e a))
      (symbol->string a)))
(define-for-syntax (sym-concat stx . a)
  (datum->syntax stx (string->symbol (apply string-append (map get-as-string a)))))

(define-for-syntax (binstruct-generate-reader-element input-port parse-type body)
  (case (syntax-e parse-type)
    [(#:guard)
     #`(unused (unless #,body
                 (error "guard failed")))]
    [(#:guard-not)
     #`(unused (when #,body
                 (error "guard failed")))]
    [(#:let)
     body]
    [(#:omit)
     #'(unused (void))]
    [else
     #`(#,body (#,parse-type #,input-port))]))
(define-syntax (binstruct stx)
  (syntax-case stx (binstruct)
    [(binstruct nameblk (parse-type elemname) ... #:return-expression expr)
     (let* ((nameblk-raw (syntax-e #'nameblk))
            (name (if (symbol? nameblk-raw) #'nameblk (first nameblk-raw)))
            (constructor-name (sym-concat stx name ':make))
            (fields-base (filter-map (lambda (elem) (and (not (keyword? (first (syntax->datum elem)))) (second (syntax-e elem))))
                                     (syntax-e #'((parse-type elemname) ...))))
            (to-omit (filter-map (lambda (elem) (and (eq? (first (syntax->datum elem)) '#:omit) (second (syntax->datum elem))))
                                 (syntax-e #'((parse-type elemname) ...))))
            (fields (filter-not (lambda (x) (member (syntax-e x) to-omit)) fields-base)))
       #`(begin
           (struct #,name #,fields #:constructor-name #,constructor-name #:omit-define-syntaxes #:inspector #f)
           (define nameblk
             (lambda (input-port)
               (let* #,(map (curry binstruct-generate-reader-element #'input-port) (syntax-e #'(parse-type ...)) (syntax-e #'(elemname ...)))
                 #,(if (eq? (syntax-e #'expr) 'default-expr)
                       (cons constructor-name fields)
                       #'expr))))))]
    [(binstruct nameblk (parse-type elemname) ...)
     #'(binstruct nameblk (parse-type elemname) ... #:return-expression default-expr)]))

; Assumes big-endian because this is for a Java class file parser.

(define (u1 port)
  (let ((byte (read-byte port)))
    (if (eof-object? byte)
        (error "unexpected eof while reading file")
        byte)))
(define (u2 port)
  (fxior (fxlshift (u1 port) 8) (u1 port)))
(define (u4 port)
  (fxior (fxlshift (u2 port) 16) (u2 port)))
(define (u8 port)
  (fxior (fxlshift (u4 port) 32) (u4 port)))
(define (array basetype count)
  (lambda (port)
    (for/list ((i (range count)))
      (basetype port))))

(define (load-file parser path #:require-eof (require-eof #t))
  (call-with-input-file path
    (lambda (port)
      (begin0 (parser port)
              (unless (or (eof-object? (read-byte port)) (not require-eof))
                (error "EOF not detected where it should have been!"))))
    #:mode 'binary))

(define (parse-flags number flags #:none (none (void)))
  (if (or (= number 0) (empty? flags)) empty
      (if (or (bitwise-bit-set? number 0) (equal? none (car flags)))
          (cons (car flags) (parse-flags (fxrshift number 1) (cdr flags)))
          (parse-flags (fxrshift number 1) (cdr flags)))))
