C
(defun our-third (x)
   (car (cdr (cdr x))))
(our-third '(a b c d))