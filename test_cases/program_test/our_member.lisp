(NIL B C)
(defun our-member (obj lst)
   (if (null lst)
       nil
   (if (eql (car lst) obj)
       lst
       (our-member obj (cdr lst)))))
(cons
    (our-member 'z '(a b c))
    (our-member 'b '(a b c))
)