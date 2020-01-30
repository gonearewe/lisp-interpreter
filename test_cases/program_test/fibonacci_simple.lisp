233
(defun fib (n)
  (cond ((eq n 0) 0)
 ((eq n 1) 1)
 (#T (+ (fib (- n 1))
              (fib (- n 2))))))
(fib 13)