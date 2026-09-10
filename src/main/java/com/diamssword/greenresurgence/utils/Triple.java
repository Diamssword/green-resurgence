package com.diamssword.greenresurgence.utils;

public class Triple<A, B, C> {
	private A left;
	private B mid;
	private C right;

	public Triple(A left, B middle, C right) {
		this.left = left;
		this.mid = middle;
		this.right = right;
	}

	public A getLeft() {
		return this.left;
	}

	public void setLeft(A left) {
		this.left = left;
	}

	public C getRight() {
		return this.right;
	}

	public void setRight(C right) {
		this.right = right;
	}

	public B getMiddle() {
		return this.mid;
	}

	public void setMiddle(B midlle) {
		this.mid = midlle;
	}
}
