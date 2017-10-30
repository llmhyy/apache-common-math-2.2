package com;

public class Example {
	public static void main(String[] args) throws Exception {
		Example ex = new Example();
		ex.foo(2, 1);
	}

	int b;
	
	public void test(int a){
		if(Math.pow(2, b) < 100){
			if(a*a > 40){
				System.currentTimeMillis();
			}
			
			System.currentTimeMillis();
		}
		
		System.currentTimeMillis();
	}
	
	public int foo(int x, int y) {
		int z = x + y;
		if (z > 0) {
			z = 1;
		}else if (z > 1) {
			z --;
		}
		if (x < 5) {
			z = -z;
		} else if (x < 4) {
			z++;
		}
		return z;
	}

	public void twoLoop(int solvedCols, int cols) throws Exception {
		boolean firstIteration = false;
		while (cols <5) {
			for (int k = 0; k < solvedCols; ++k) {
			}

			cols++;
			if (cols > 6) {
				firstIteration = true;
			}

			if (firstIteration) {
				throw new Exception();
			}
		}
	}

	public void twoLoop1(int solvedCols, int cols) throws Exception {
		boolean firstIteration = false;
		for (int k = 0; k < solvedCols; ++k) {
		}
		if (cols > 6) {
			firstIteration = true;
		}else if (cols > 7) {
			if (firstIteration) {
				cols++;
			}
		}
	}

	public void setB(int b) {
		this.b = b;
	}

}
