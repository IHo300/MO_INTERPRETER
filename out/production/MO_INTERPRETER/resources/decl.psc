func int fib(int x) {

    if (x == 0) then {
        return 0;
    } else if (x == 1) then {
        return 1;
    } else if (x == 2) then {
		return 1;
	}

	int first = fib(x-2);
	int second = fib(x-1);

    return first + second;
}

main() {
  int m = fib(5);

    print(m);
}