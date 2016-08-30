package com.example.utils;

import android.app.Activity;

class FFF extends Activity {

	public String ISexist(String str, String str1) {
		String result = "";

		for (int i = 0; i < str.length(); i++) {
			for (int j = 0; j < str1.length(); j++) {
				if (str.charAt(i) == str1.charAt(j)) {
					result = result + str.charAt(i);
				}
			}
		}

		return result;
	}
}
