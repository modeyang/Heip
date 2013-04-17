package com.test;

import java.util.Random;

public class TestRandom {
	public static void main(String args[]){
		Random r = new Random();
		int a = r.nextInt(1000);
		System.out.println(a);
	}
}
