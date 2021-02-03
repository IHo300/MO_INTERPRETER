

main() {


    int max;

    scan("How many numbers?: ", max);

    int[] arr = create int[max];
    for int i = 0 up to max {
        int x;
        scan("Enter number: ", x);
        arr[i] = x;
    }

    for int k = 0 up to max - 1 {

        for int j = 0 up to max-k-1 {
            if (arr[j] > arr[j+1]) then {
                int temp = arr[j];
                arr[j] = arr[j+1];
                arr[j+1] = temp;
            }
        }
    }

    int w = 0;
    while w up to max {
        int s = arr[w];
        print(s);
    }

} 