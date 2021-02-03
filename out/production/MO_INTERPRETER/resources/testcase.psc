func bool assertNonZero(int x) {
    if(x > 0) then {
        return T;
    }
    
    //error #1
}

func void sayHelloWorld() {
    print("Hello World");
}

//error #2
func void sayHelloWorld() {
    print("Hello World); //error #3
}

func void testPrint() {

    for int i = 0 up to (5 * 2 { //error #4
        print("Petmalu " +z+); //error #5
     
    
    //error #6 (missing curly brace)
    
    return 10; //error #7
}

func void arrayTest(int size_1, int size_2) {
    int[] simpleArray = create int[size_1];
    int[] simpleArray = create int[size_2]; //error #8
    int[] simpleArray_2 = create int[2.0f]; //error #9
    
    int complexArraySize = (size_1 * 2) - size_2 - 4;
    int[] complexArray = create int[complexArraySize];
    
    for int i = 0 up to complexArraySize {
        complexArraySize[i] = i;
    }
    
    return complexArray; //error #10
}

main() {
    constant int PI = 3.14f; //error #11
    int x = 0;
    int y = 5;
    int z = 13;
        
    z = assertNonZero(x); //error #12
    z += x += 5; //error  #13
    z +++; //error #14
    
    if(z > 0) then {
        print("Computated value: " + someValue()); //error #15
        print("Computed value: " + someValue);
    }
    
    int z = 0; // syntax error #16,
    int x = 0; //  syntax error #17
    
    z * 10; //error #18
    z = x ++ (y + "10"); //error #19
    PI = x + y; //error #20 
    
    sayHelloWorld()(); //error #21
    print(Finished execution); //error #22
    
    
}