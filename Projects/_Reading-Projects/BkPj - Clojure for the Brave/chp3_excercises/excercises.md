
Chapter 3 Exercises 

1. Use the str, vector, list, hash-map, and hash-set functions.

2. Write a function that takes a number and adds 100 to it. 

3. Write a function, dec-maker, that works exactly like the function inc-maker except with the subtraction>
    (def dec9 (dec-maker 9))
    (dec9 10)
    => 1

4. Write a function, mapset, that works like map except the return value is a set:
    (mapset inc [1 1 2 2])
    => #{2 3}

5. Create a function thats similiar to symmetrize-body-parts except that it has to work with weird 
   space aliens with radial symmetry. Instead of two eyes, arms, legs, and so on, they have five. 

6. Create a function that generalizes symmetrize-body-parts and the function that you created in Exercise 5. 
   The new function should take a collection of body parts and the number of mathching body parts to add. If 
   you're completely new ot Lisp Languages and functional programming, it probably wont be obvious how to do 
   this.