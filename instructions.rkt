#lang racket

(provide i/mov i/call i/dyncall i/return i/const-int i/get-static i/put-static)

(struct i/mov (from to) #:inspector #f)
(struct i/call (target-ref inputs output) #:inspector #f)
(struct i/dyncall (target inputs output) #:inspector #f)
(struct i/return (from) #:inspector #f)
(struct i/const-int (ref value) #:inspector #f)
(struct i/get-static (ref glob-ref width) #:inspector #f)
(struct i/put-static (ref glob-ref width) #:inspector #f)
