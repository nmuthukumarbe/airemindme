package com.server.realsync.config;

public class Solution {
	public int solution(String S) {
		int count = 0;
        int N = S.length();
        
        // Iterate for N-1 rotations
        for (int i = 0; i < N; i++) {
            // Get the first and last letters of the current string
            char first = S.charAt(0);
            char last = S.charAt(N - 1);
            
            // Check if the first and last characters are the same
            if (first == last) {
                count++;
            }
            
            // Rotate the string by moving the first character to the end
            S = S.substring(1) + first;
        }
        
        return count;

	}

	public static void main(String args[]) {
		Solution solution = new Solution();
		System.out.println(solution.solution("abab"));
	}
}