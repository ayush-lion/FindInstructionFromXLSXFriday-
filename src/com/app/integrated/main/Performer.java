package com.app.integrated.main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.app.abacus.panel.AbacusPanel;
import com.app.abacus.panel.exception.AbacusException;
import com.app.instruction.panel.InstructionPanel;
import com.app.instructions.beans.Action;
import com.app.sound.SpeechController;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class Performer implements Runnable {

	LinkedHashMap<String, HashMap<String, List<Action>>> data;

	private Thread readerThread;
	InstructionPanel instructionPanel;
	AbacusPanel abacusPanel;
	private SpeechController playSound;
	private static final String VOICE = "kevin16";
	private VoiceManager vm = null;
	private Voice voice = null;
	private boolean isPlayRobotics;

	/**
	 * @return the isPlayRobotics
	 */
	public boolean isPlayRobotics() {
		return isPlayRobotics;
	}

	/**
	 * @param isPlayRobotics
	 *            the isPlayRobotics to set
	 */
	public void setPlayRobotics(boolean isPlayRobotics) {
		this.isPlayRobotics = isPlayRobotics;
	}

	/**
	 * @return the isPlayNatural
	 */
	public boolean isPlayNatural() {
		return isPlayNatural;
	}

	/**
	 * @param isPlayNatural
	 *            the isPlayNatural to set
	 */
	public void setPlayNatural(boolean isPlayNatural) {
		this.isPlayNatural = isPlayNatural;
	}

	private boolean isPlayNatural;

	public Performer() {
		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
		isPlayRobotics = false;
		isPlayNatural = false;

	}

	/**
	 * @return the instructionPanel
	 */
	public InstructionPanel getInstructionPanel() {
		return instructionPanel;
	}

	/**
	 * @param instructionPanel
	 *            the instructionPanel to set
	 */
	public void setInstructionPanel(InstructionPanel instructionPanel) {
		this.instructionPanel = instructionPanel;
	}

	/**
	 * @return the abacusPanel
	 */
	public AbacusPanel getAbacusPanel() {
		return abacusPanel;
	}

	/**
	 * @param abacusPanel
	 *            the abacusPanel to set
	 */
	public void setAbacusPanel(AbacusPanel abacusPanel) {
		this.abacusPanel = abacusPanel;
	}

	/**
	 * @return the data
	 */
	public LinkedHashMap<String, HashMap<String, List<Action>>> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(LinkedHashMap<String, HashMap<String, List<Action>>> data) {
		this.data = data;
	}

	public void startReading() {
		readerThread = new Thread(this);
		readerThread.start();
	}

	private void playRoboticsVoice(String insTxt) {
		String[] txt = insTxt.split("\n");
		String txtInput = "";
		for (String data : txt) {
			if (!data.trim().equalsIgnoreCase("")) {
				txtInput = txtInput + data + " ";
			}
		}
		try {
			/** Setting up voice manager */
			vm = VoiceManager.getInstance();
			voice = vm.getVoice("kevin16");
			voice.allocate();
			voice.speak(txtInput);
			voice.deallocate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// setDataReadyForRead(false);
		}
	}

	private void playText(String insTxt, int counter) {
		String[] txt = insTxt.split("\n");
		String txtInput = "";
		for (String data : txt) {
			if (!data.trim().equalsIgnoreCase("")) {
				txtInput = txtInput + data + " \\pau=1000\\ ";
			}
		}

		playSound = new SpeechController();
		try {
			// playSound.Speak("sharon22k", txtInput);
			playSound.playSound("/audio/" + counter + ".wav");
			// setDataReadyForRead(false);
		} catch (IllegalArgumentException e1) { /** Eating exceptions */
		} catch (IllegalStateException e1) {
			/** Eating exceptions */
		}
	}

	/**
	 * @return the playSound
	 */
	public SpeechController getPlaySound() {
		return playSound;
	}

	/**
	 * @param playSound
	 *            the playSound to set
	 */
	public void setPlaySound(SpeechController playSound) {
		this.playSound = playSound;
	}

	public void stopPlayback() {
		if (readerThread.isAlive()) {
			readerThread.stop();
			if (getPlaySound() != null && getPlaySound().getClip() != null) {
				getPlaySound().stopClip();
			}

		}
	}

	/** list of actions into an HashMap */

	public void start_beforInstructing() throws AbacusException {
		Set<Entry<String, HashMap<String, List<Action>>>> entrySet = data.entrySet();
		int i = 0;
		HashMap<String, String> actionMap = new HashMap<String, String>();
		for (Entry<String, HashMap<String, List<Action>>> entry : entrySet) {

			Object[] tableRow = new Object[9];
			String key = entry.getKey();
			tableRow[0] = key;
			HashMap<String, List<Action>> map = entry.getValue();
			Set<Entry<String, List<Action>>> sEntry = map.entrySet();
			for (Entry<String, List<Action>> entry2 : sEntry) {
				i++;
				String instruction = entry2.getKey();
				instructionPanel.performinstruction(instruction, instructionPanel);
				playRoboticsVoice(instruction);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				List<Action> listOfActions = entry2.getValue();
			
				for (Action  actionlist: listOfActions) {
					
					if(actionlist.getActionName().equals("Font"))
					{
						actionlist.getFont();
						actionMap.put("	Font Name", actionlist.getFont().getName());
						actionMap.put("Font Size", actionlist.getFont().getSize());
					}
					
					if(actionlist.getActionName().equals("Layout")) 
					{
						actionlist.getLayout();
						actionMap.put("NumOfRows", String.valueOf(actionlist.getLayout().getNumOfRow()));
						actionMap.put("NumOfCols", String.valueOf(actionlist.getLayout().getNumOfCols()));
						
						actionlist.getLayout().getRows();
						for (int j = 0; j < actionlist.getLayout().getRows().size(); j++)
						{
						actionMap.put("row"+j+"Height", actionlist.getLayout().getRows().get(j).getHeight());	
						actionMap.put("row"+j+"Weight", actionlist.getLayout().getRows().get(j).getWidth());
						}				
					}
	
					if(actionlist.getActionName().equals("Component")) 
					{
						actionlist.getComponent();
						actionMap.put("RodNumber", actionlist.getComponent().getNumOfRods());
						actionMap.put("Component Name",actionlist.getComponent().getName());
						
						actionMap.put("Component Name",actionlist.getComponent().getName());
						actionMap.put("Teacher Align", actionlist.getComponent().getTeacherAlign());
						actionMap.put("Student Align", actionlist.getComponent().getStudentAlign());
						
						actionMap.put("Component Name", actionlist.getComponent().getName());
						actionMap.put("Align", actionlist.getComponent().getAlign());
						
						actionMap.put("Component Name", actionlist.getComponent().getImage());
					}
				}		
			}
		}
		System.out.println("data fileeeeddddd"+actionMap);
	}

	/** start instructions */

	public void start_instructing() throws AbacusException {
		Set<Entry<String, HashMap<String, List<Action>>>> entrySet = data.entrySet();
		int i = 0;					
		for (Entry<String, HashMap<String, List<Action>>> entry : entrySet) {
			Object[] tableRow = new Object[10];
			String key = entry.getKey();
			tableRow[0] = key;

			HashMap<String, List<Action>> map = entry.getValue();

			Set<Entry<String, List<Action>>> sEntry = map.entrySet();
			for (Entry<String, List<Action>> entry2 : sEntry) {
				i++;
				String instruction = entry2.getKey();
				instructionPanel.performinstruction(instruction, instructionPanel);
				playRoboticsVoice(instruction);

				// System.out.println("voice " + i);
				// playText(instruction, i);

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				List<Action> listOfActions = entry2.getValue();
			
				
				StringBuffer actBuf = new StringBuffer();	
				for (Action action : listOfActions) {
					//System.out.println(listOfActions);
					if (action.getActionName().contains("HighlightFrame")) {
						abacusPanel.highlightFrame();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					} else if (action.getActionName().contains("HighlightRods")) {
						abacusPanel.highlightRods();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					} else if (action.getActionName().contains("HighlightBeam")) {
						abacusPanel.highlightLowerBeads();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					} else if (action.getActionName().contains("AddRod")) {
						abacusPanel.moveEarthBeadUp(action.getRodNumber(), action.getBeadNumber(), action.getFinger());
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					} else if (action.getActionName().contains("MinusRod")) {
						abacusPanel.moveEarthBeadDown(action.getRodNumber(), action.getBeadNumber(),
								action.getFinger());
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}

				}

			}
			// odel.addRow(tableRow);
		}
	}

	@Override
	public void run() {
		try {
			start_beforInstructing();
		} catch (AbacusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}

}
