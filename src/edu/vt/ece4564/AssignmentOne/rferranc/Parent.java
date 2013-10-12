package edu.vt.ece4564.AssignmentOne.rferranc;

import java.util.ArrayList;

// Example from http://myandroidsolutions.blogspot.com/2012/08/android-expandable-list-example.html
public class Parent {
	private String mTitle;
    private ArrayList<String> mArrayChildren;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public ArrayList<String> getArrayChildren() {
        return mArrayChildren;
    }

    public void setArrayChildren(ArrayList<String> mArrayChildren) {
        this.mArrayChildren = mArrayChildren;
    }
    
    public void addArrayChild(String child) {
    	this.mArrayChildren.add(child);
    }
}
