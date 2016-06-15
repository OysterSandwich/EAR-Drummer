package genetic.rules;

import genetic.DrumPattern;
import input.InputAnalysis;

public class LoudnessRule extends Rule {
	
	public LoudnessRule() {
		super("Loudness", null);
	}

	@Override
	public void rate(DrumPattern pattern, InputAnalysis analysis) {
			
		//bewertet pattern hoch, die den gleichen lautstärke durchschnitt wie das solo haben
		
		if (analysis.numberOfNotes <= 0) {
			return;
		}
		
		int count = 0;
		float sum = 0;
		for (int i = 0; i < pattern.matrix.length; i++) {
			for (int j = 0; j < pattern.matrix[i].length; j++) {
				
				if (pattern.matrix[i][j] > 0) {
					count++;
					sum += pattern.matrix[i][j];
				}
			}
		}
		float average;
		if (count > 0)
			average = sum / count;
		else
			average = 0;
		
		float distance = Math.abs(analysis.volumeAverage - average);
		
		float rate = LIMIT - (LIMIT * distance / 127);
		
		rateWeighted(pattern, rate);
	}
	
}