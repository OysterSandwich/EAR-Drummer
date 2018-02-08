package init;

import javax.swing.JOptionPane;

import genetic.DrumPattern;
import genetic.Evolution;
import genetic.FitnessEvaluator;
import genetic.Generation;
import genetic.combine.CombiManager;
import genetic.mutations.MutationManager;
import genetic.rules.RuleManager;
import gui.CombiManagerFrame;
import gui.DrumPatternFrame;
import gui.EvolutionFrame;
import gui.FrameManager;
import gui.InputManagerFrame;
import gui.InputWindowFrame;
import gui.LoadSavePanel;
import gui.MetronomeFrame;
import gui.MidiKeyboardDummyFrame;
import gui.OutputManagerFrame;
import gui.RuleManagerFrame;
import gui.SimpleBassistFrame;
import gui.SoloRecorderFrame;
import input.InputManager;
import input.InputReceiver;
import input.InputWindow;
import output.DrumGenerator;
import output.OutputGenerator;
import output.OutputManager;
import playback.Metronome;
import playback.PatternPlayer;
import record.SoloRecorder;
import bass.SimpleBassist;

public class Init {
	
	public static void main(String[] args) {
		
		try {
			
			if (args.length > 0) {
				for (String s : args) {
					if (s.equals("--debug"))
						Settings.DEBUG = true;
				}
			}
			if (Settings.DEBUG) {
				Streams.debugStream.println("debug stream enabled");
			}
			else {
				Streams.debugStream.println("debug stream is disabled, use argument '--debug' to enable");
				Streams.debugStream.close();
			}
			
			
			//TODO more settings, static or dynamic
			//needs to be able to set up following (maybe online?): Settings.Ticks, RhythmNote.numberOfDrums, default pattern!
			
			//*************************************
			// initiate all frames and components
			//*************************************
			InputWindow inputWindow = new InputWindow();
			InputReceiver inputReceiver = new InputReceiver(inputWindow);
			InputManager inputManager = new InputManager(inputReceiver);
			new InputManagerFrame(inputManager);
			new InputWindowFrame(inputWindow);
			
			OutputGenerator outputGenerator = new OutputGenerator();
			OutputManager outputManager = new OutputManager(outputGenerator);
			new OutputManagerFrame(outputManager);
			
			Metronome metronome = new Metronome(Settings.TICKS, Settings.TPM, Settings.SWING);
			new MetronomeFrame(metronome);
			DrumGenerator drumGenerator = new DrumGenerator(outputGenerator);
			PatternPlayer patternPlayer = new PatternPlayer(drumGenerator);
			metronome.addMetronomeListener(patternPlayer);
			
			SoloRecorder soloRecorder = new SoloRecorder(inputManager, outputManager, metronome);
			inputReceiver.setRecorder(soloRecorder);
			new SoloRecorderFrame(soloRecorder);
			
			DrumPattern pattern = new DrumPattern(Settings.TICKS);
			
			new DrumPatternFrame(pattern, metronome, true);
			
			DrumPatternFrame currentPatternFrame = new DrumPatternFrame(pattern, metronome, false);
			patternPlayer.addObserver(currentPatternFrame);
			
			Generation.setInitPattern(pattern);
			
			MutationManager mutationManager = new MutationManager();
			// mutation manager currently just uses one mutation so frame is unnecessary
//			new MutationManagerFrame(mutationManager);
			
			FitnessEvaluator fitnessEvaluator = null;
			RuleManager ruleManager = null;
			CombiManager combiManager = null;
			
			if (Settings.FITNESS_VERSION == Settings.FitnessVersion.RuleBased) {
				fitnessEvaluator = ruleManager = new RuleManager();
				new RuleManagerFrame(ruleManager);
			}
			else if (Settings.FITNESS_VERSION == Settings.FitnessVersion.CombinationBased) {
				fitnessEvaluator = combiManager = new CombiManager();
				new CombiManagerFrame(combiManager);
			} else {
				throw new IllegalStateException("Unknown fitness version: " + Settings.FITNESS_VERSION);
			}
			
			Evolution evolution = new Evolution(inputWindow, fitnessEvaluator, mutationManager);
			new EvolutionFrame(evolution);
			
			SimpleBassist simpleBassist = new SimpleBassist();
			metronome.addMetronomeListener(simpleBassist);
			SimpleBassistFrame simpleBassistFrame = new SimpleBassistFrame(simpleBassist);
			
			// TODO work on Bassist
//			BassGenerator bassGenerator = new BassGenerator(outputGenerator);
//			PrimitiveBassist bassist = new PrimitiveBassist(bassGenerator, evolution);
//			metronome.addMetronomeListener(bassist);
//			new PrimitiveBassistFrame(bassist);
			
			new MidiKeyboardDummyFrame(outputManager, inputManager);
			
			//**********************
			// after frame creating
			//**********************
			
			LoadSavePanel.loadAllDefaultFiles();
			FrameManager.getInstance().rearrange();
		
		} catch (Exception e) {
			if (Settings.DEBUG) {
				e.printStackTrace();
				System.exit(1);
			} else {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
	}

}
