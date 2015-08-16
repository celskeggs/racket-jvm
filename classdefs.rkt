#lang racket


(provide jclass jfield jmethod jtype-desc primitive-types jclasstype jarraytype jtype-primitive? jtype-name jtype-class? jtype-array?)

(struct jclass (name super fields methods) #:inspector #f)
(struct jfield (name type visibility constant-value is-static is-final) #:inspector #f)
(struct jmethod (name type visibility is-static code) #:inspector #f)

; TYPES

(struct jtype (name desc) #:inspector #f)
(struct jtype-class jtype () #:inspector #f)
(struct jtype-array jtype (base) #:inspector #f)
(define (jclasstype name)
  (let ((sym-name (if (symbol? name) name (string->symbol name)))
        (str-name (if (symbol? name) (symbol->string name) name)))
    (jtype-class sym-name (string-append "L" str-name ";"))))
(define (jarraytype elem)
  (jtype-array (string->symbol (string-append (symbol->string (jtype-name elem)) "[]"))
               (string-append "[" (jtype-desc elem))
               elem))

(struct jtype-primitive jtype () #:inspector #f)
(define (def-jprim name desc)
  (let ((vname (string->symbol (string-append "j" (symbol->string name)))))
    (let-values ([(strtype constr inst? acs mut) (make-struct-type vname struct:jtype-primitive 0 0 #f empty #f #f empty #f vname)])
      (constr name desc))))

(provide jbyte jchar jdouble jfloat jint jlong jshort jbool jvoid primitive-types primitive-type-descriptors)
(define jbyte (def-jprim 'byte "B"))
(define jchar (def-jprim 'char "C"))
(define jdouble (def-jprim 'double "D"))
(define jfloat (def-jprim 'float "F"))
(define jint (def-jprim 'int "I"))
(define jlong (def-jprim 'long "J"))
(define jshort (def-jprim 'short "S"))
(define jbool (def-jprim 'bool "Z"))
(define jvoid (def-jprim 'void "V"))
(define primitive-types (list jbyte jchar jdouble jfloat jint jlong jshort jbool jvoid))
(define primitive-type-descriptors (map jtype-desc primitive-types))
