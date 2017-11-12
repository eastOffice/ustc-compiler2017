define i32 @fib(i32) {
    %2 = alloca i32, align 4                ; return value
    %3 = icmp eq i32 %0, 0                  ; if n == 0
    br i1 %3, label %4, label %5

; <label>:4:                                 ; n == 0
  store i32 0, i32* %2, align 4              ; set return value = 0
  br label %return

; <label>:5:                                   
  %6 = icmp eq i32 %0, 1                      ; if n == 1
  br i1 %6, label %8, label %9                  

return:                                       
  %7 = load i32, i32* %2, align 4       
  ret i32 %7

; <label>:8:                                   ; n == 1                 
  store i32 1, i32* %2, align 4                ; set return value = 1
  br label %return                             

; <label>:9:                                      
  %10 = sub nsw i32 %0, 1
  %11 = call i32 @fib(i32 %10)
  %12 = sub nsw i32 %0, 2
  %13 = call i32 @fib(i32 %12)
  %14 = add nsw i32 %11, %13
  store i32 %14, i32* %2, align 4
  br label %return
}

define i32 @main() {
    %1 = alloca i32, align 4
    %2 = alloca i32, align 4                    ; x return value
    %3 = alloca i32, align 4                    ; i = 0
    store i32 0, i32* %1, align 4
    store i32 0, i32* %2, align 4
    store i32 0, i32* %3, align 4
    br label %loop

loop:                                     
  %4 = load i32, i32* %3, align 4
  %5 = icmp slt i32 %4, 10                      ; if i < 10
  br i1 %5, label %6, label %return

; <label>:6:                                      
  %7 = load i32, i32* %3, align 4
  %8 = call i32 @fib(i32 %7)
  %9 = load i32, i32* %2, align 4
  %10 = add nsw i32 %9, %8
  store i32 %10, i32* %2, align 4                                
  %11 = add nsw i32 %7, 1                      ; i = i+1
  store i32 %11, i32* %3, align 4
  br label %loop

return:                                 
  %12 = load i32, i32* %2, align 4
  ret i32 %12
}

