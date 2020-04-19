package ddnet.useragent.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Hashtable;
import java.util.UUID;

import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ddnet.useragent.App;
import ddnet.useragent.audio.Recording.EventListener;
import ddnet.useragent.audio.VorbisEncoding;
import ddnet.useragent.integration.DDNETServicesProxy;
import ddnet.useragent.util.UIUtils;

public class RecordingForm extends Form {
	private static final long serialVersionUID = 1L;	
			
	private static final String ACTION_COMMAND_RECORD_AUDIO = "record";
	private static final String ACTION_COMMAND_STOP = "stop";
	private static final String ACTION_COMMAND_PLAY = "play";
	private static final String ACTION_COMMAND_PAUSE = "pause";
	private static final String ACTION_COMMAND_STEP_BACK = "step-back";
	private static final String ACTION_COMMAND_STEP_FORWARD = "step-forward";
	private static final String ACTION_COMMAND_REWIND = "rewind";
	private static final String ACTION_COMMAND_FAST_FORWARD = "fast-forward";
	private static final String ACTION_COMMAND_CANCEL = "cancel";
	private static final String ACTION_COMMAND_SAVE = "save";
	private static final int BROWSE_INTERVAL_MILLIS = 5000;
	
	private boolean focusSet;
	private final RecordingModel model;
	
	private JButton recordAudioButton;
	private JButton stopButton;
	private JButton playButton;
	private JButton pauseButton;
	private JButton stepBackButton;
	private JButton stepForwardButton;
	private JButton rewindButton;
	private JButton fastForwardButton;
	private JButton cancelButton;
	private JButton saveButton;

	private JSlider positionSlider;

	private final ActionListener formActionListener;

	public RecordingForm(RecordingModel model) {
		if (model == null)
			throw new IllegalArgumentException("model");
		
		this.model = model;
				
		this.formActionListener = new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ACTION_COMMAND_RECORD_AUDIO.equals(e.getActionCommand())) {
					try {
						getModel().getRecording().beginRecording();
					} catch (Throwable t) {
						JOptionPane.showMessageDialog(null, "Error grabando audio: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (ACTION_COMMAND_STOP.equals(e.getActionCommand())) {
					try {
						if (getModel().getRecording().isRecording())
							getModel().getRecording().endRecording();
						else
							getModel().getRecording().stopPlaying();
					} catch (Throwable t) {
						JOptionPane.showMessageDialog(null, "Error deteniendo audio: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (ACTION_COMMAND_PLAY.equals(e.getActionCommand())) {
					try {
						getModel().getRecording().startPlaying();
					} catch (Throwable t) {
						JOptionPane.showMessageDialog(null, "Error reproduciendo audio: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (ACTION_COMMAND_PAUSE.equals(e.getActionCommand())) {
					try {
						getModel().getRecording().pausePlaying();
					} catch (Throwable t) {
						JOptionPane.showMessageDialog(null, "Error reproduciendo audio: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (ACTION_COMMAND_STEP_BACK.equals(e.getActionCommand())) {
					try {
						getModel().getRecording().setPosition(0);
					} catch (Throwable t) {
						JOptionPane.showMessageDialog(null, "Error reproduciendo audio: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (ACTION_COMMAND_STEP_FORWARD.equals(e.getActionCommand())) {
					try {
						getModel().getRecording().setPosition(getModel().getRecording().getLength());
					} catch (Throwable t) {
						JOptionPane.showMessageDialog(null, "Error reproduciendo audio: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (ACTION_COMMAND_REWIND.equals(e.getActionCommand())) {
					try {
						getModel().getRecording().setPosition(getModel().getRecording().getPosition() - BROWSE_INTERVAL_MILLIS);
					} catch (Throwable t) {
						JOptionPane.showMessageDialog(null, "Error reproduciendo audio: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (ACTION_COMMAND_FAST_FORWARD.equals(e.getActionCommand())) {
					try {
						getModel().getRecording().setPosition(getModel().getRecording().getPosition() + BROWSE_INTERVAL_MILLIS);
					} catch (Throwable t) {
						JOptionPane.showMessageDialog(null, "Error reproduciendo audio: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (ACTION_COMMAND_SAVE.equals(e.getActionCommand())) {
					try {
						
						//JOptionPane.showMessageDialog(null,RecordingForm.class.getProtectionDomain().getCodeSource().getLocation());
						//JOptionPane.showMessageDialog(null,VorbisEncoding.class.getProtectionDomain().getCodeSource().getLocation());
						
						// TODO: mover codigo de conversion a la clase Recording???
						File outputFilename = getModel().getRecording().getOutputFile();
						File convertedFilename = new File(getModel().getRecording().getOutputFile().getParent(), UUID.randomUUID().toString() + ".ogg");
						VorbisEncoding.createCodec().encode(outputFilename.getAbsolutePath(), convertedFilename.getAbsolutePath());
						
//						ClipboardUtils.setClipboardContent(convertedFilename.getAbsolutePath());
//						JOptionPane.showMessageDialog(null, "Archivo grabado en: " + convertedFilename.getAbsolutePath() + ". El path completo fue copiado al portapapeles.", 
//								"Aviso", JOptionPane.INFORMATION_MESSAGE);
						
						// TODO: Ver porque hay que incluir la "/" final para que funcione luego la "concatenacion" de URLs.
						DDNETServicesProxy proxy = new DDNETServicesProxy
								(new URL("http://"+getModel().getHost()+"/ddnet-web/"), App.getInstance().getUserAgent().getCurrentUser().getToken());
						proxy.uploadStudyFile(getModel().getStudyID(), convertedFilename);
						
						//closeWindow();
						JOptionPane.showMessageDialog(null,"Se ha guardado el audio", "HECHO", JOptionPane.INFORMATION_MESSAGE);
						
					} catch (Throwable t) {
						JOptionPane.showMessageDialog(null, "Error: " + t.getMessage() +"\n"+t.toString(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (ACTION_COMMAND_CANCEL.equals(e.getActionCommand())) {
					closeWindow();
				}
			}
		};
		configureForm();
		registerEventsHandler();
		getModel().getRecording().requestStatus();
	}
	
	private void registerEventsHandler() {
		addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		    	if (!focusSet) {
		    		recordAudioButton.requestFocusInWindow();
		    		focusSet = true;
		    	}
		    }
		});		
		
		getModel().getRecording().addListener(new EventListener() {			
			@Override
			public void update(final boolean empty, final boolean recording, final boolean playing, final int lengthMillis, final int positionMillis) {				
				boolean atBeginning = getModel().getRecording().atBeginning();
				boolean atEnd = getModel().getRecording().atEnd();
				
				recordAudioButton.setEnabled(!recording && !playing);				
				stopButton.setEnabled(recording || playing);
				playButton.setEnabled(!recording && !playing && !empty && !atEnd);
				pauseButton.setEnabled(playing);								
				stepBackButton.setEnabled(!recording && !playing && !empty && !atBeginning);
				stepForwardButton.setEnabled(!recording && !playing && !empty && !atEnd);
				rewindButton.setEnabled(!recording && !playing && !empty && !atBeginning);
				fastForwardButton.setEnabled(!recording && !playing && !empty && !atEnd);				
				saveButton.setEnabled(!recording && !playing && !empty);
				cancelButton.setEnabled(!recording && !playing);
								
				positionSlider.setEnabled(!empty && !playing && !recording);
				if (empty) {
					positionSlider.setMinimum(0);
					positionSlider.setMaximum(0);
					positionSlider.setValue(0);
				} else {
					positionSlider.setMinimum(0);
					positionSlider.setMaximum(lengthMillis);
					positionSlider.setValue(positionMillis);
					
					Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
					labelTable.put(new Integer(0), new JLabel(millisToTime(0)));
					labelTable.put(new Integer(lengthMillis), new JLabel(millisToTime(lengthMillis > 0 ? lengthMillis : 0)));
					positionSlider.setLabelTable(labelTable);
					positionSlider.setPaintLabels(true);
				}
			}
		});
	}

	private void configureForm() {
		setSize(900, 120);
		setTitle(String.format("2016.08.25 - Dictado de informe - %s", model.getPatientName()));
		setLocationRelativeTo(null); // Centra el form en la pantalla.
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		final Dimension buttonDimension = new Dimension(60, 35);
		
		final GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = -1;
		constraints.gridy = -1;
		constraints.insets = new Insets(2, 2, 2, 2);

		JPanel commandsPanel = new JPanel(layout);
		
		// Fila 0
		constraints.gridy++;
		constraints.gridx++;
		constraints.weightx = 0.0;
		constraints.gridwidth = 11;
		commandsPanel.add(new JLabel(String.format("ESTUDIO: %s", model.getStudyDescription())), constraints);
		
		// Fila 1
		constraints.gridx = -1;
		constraints.gridy++;
		
		recordAudioButton = new JButton("F1");
		recordAudioButton.addKeyListener(this);
		recordAudioButton.setActionCommand(ACTION_COMMAND_RECORD_AUDIO);
		recordAudioButton.addActionListener(formActionListener);
		recordAudioButton.setIcon(Icons.RECORD);
		recordAudioButton.setPreferredSize(buttonDimension);
		constraints.weightx = 0.5;
		constraints.gridwidth = 1;
		constraints.gridx++;
		commandsPanel.add(recordAudioButton, constraints);		

		stopButton = new JButton("F2");
		stopButton.addKeyListener(this);
		stopButton.setActionCommand(ACTION_COMMAND_STOP);
		stopButton.addActionListener(formActionListener);
		stopButton.setIcon(Icons.STOP);
		stopButton.setPreferredSize(buttonDimension);
		constraints.weightx = 0.5;
		constraints.gridwidth = 1;
		constraints.gridx++;
		commandsPanel.add(stopButton, constraints);

		playButton = new JButton("F3");
		playButton.addKeyListener(this);
		playButton.setActionCommand(ACTION_COMMAND_PLAY);
		playButton.addActionListener(formActionListener);
		playButton.setIcon(Icons.PLAY);
		playButton.setPreferredSize(buttonDimension);
		constraints.weightx = 0.5;
		constraints.gridwidth = 1;
		constraints.gridx++;
		commandsPanel.add(playButton, constraints);

		pauseButton = new JButton("F4");
		pauseButton.addKeyListener(this);
		pauseButton.setActionCommand(ACTION_COMMAND_PAUSE);
		pauseButton.addActionListener(formActionListener);
		pauseButton.setIcon(Icons.PAUSE);
		pauseButton.setPreferredSize(buttonDimension);
		constraints.weightx = 0.5;
		constraints.gridwidth = 1;
		constraints.gridx++;
		commandsPanel.add(pauseButton, constraints);		
				
		stepBackButton = new JButton("F5");
		stepBackButton.addKeyListener(this);
		stepBackButton.setActionCommand(ACTION_COMMAND_STEP_BACK);
		stepBackButton.addActionListener(formActionListener);
		stepBackButton.setIcon(Icons.STEP_BACK);
		stepBackButton.setPreferredSize(buttonDimension);
		constraints.weightx = 0.5;
		constraints.gridwidth = 1;
		constraints.gridx++;
		commandsPanel.add(stepBackButton, constraints);

		rewindButton = new JButton("F6");
		rewindButton.addKeyListener(this);
		rewindButton.setActionCommand(ACTION_COMMAND_REWIND);
		rewindButton.addActionListener(formActionListener);
		rewindButton.setIcon(Icons.REWIND);
		rewindButton.setPreferredSize(buttonDimension);
		constraints.weightx = 0.5;
		constraints.gridwidth = 1;
		constraints.gridx++;
		commandsPanel.add(rewindButton, constraints);
		
		fastForwardButton = new JButton("F7");
		fastForwardButton.addKeyListener(this);
		fastForwardButton.setActionCommand(ACTION_COMMAND_FAST_FORWARD);
		fastForwardButton.addActionListener(formActionListener);
		fastForwardButton.setIcon(Icons.FAST_FORWARD);
		fastForwardButton.setPreferredSize(buttonDimension);
		constraints.weightx = 0.5;
		constraints.gridwidth = 1;
		constraints.gridx++;
		commandsPanel.add(fastForwardButton, constraints);
		
		stepForwardButton = new JButton("F8");
		stepForwardButton.addKeyListener(this);
		stepForwardButton.setActionCommand(ACTION_COMMAND_STEP_FORWARD);
		stepForwardButton.addActionListener(formActionListener);
		stepForwardButton.setIcon(Icons.STEP_FORWARD);
		stepForwardButton.setPreferredSize(buttonDimension);
		constraints.weightx = 0.5;
		constraints.gridwidth = 1;
		constraints.gridx++;
		commandsPanel.add(stepForwardButton, constraints);
				
		saveButton = new JButton("F11");
		saveButton.addKeyListener(this);
		saveButton.setActionCommand(ACTION_COMMAND_SAVE);
		saveButton.addActionListener(formActionListener);
		saveButton.setIcon(Icons.SAVE);
		saveButton.setPreferredSize(buttonDimension);
		constraints.weightx = 0.5;
		constraints.gridwidth = 1;
		constraints.gridx++;
		commandsPanel.add(saveButton, constraints);		

		cancelButton = new JButton("F12");
		cancelButton.addKeyListener(this);
		cancelButton.setActionCommand(ACTION_COMMAND_CANCEL);
		cancelButton.addActionListener(formActionListener);
		cancelButton.setIcon(Icons.CANCEL);
		cancelButton.setPreferredSize(buttonDimension);
		constraints.weightx = 0.5;
		constraints.gridwidth = 1;
		constraints.gridx++;
		constraints.gridx++;
		commandsPanel.add(cancelButton, constraints);		
		
		// Fila 5
		constraints.gridx = -1;
		constraints.gridy++;
		
		positionSlider = new JSlider();
		positionSlider.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!getModel().getRecording().isPlaying() && !getModel().getRecording().isRecording())
					getModel().getRecording().setPosition(positionSlider.getValue());
			}
		});		
		constraints.gridx++;
		constraints.weightx = 1.0;
		constraints.gridwidth = 11;
		commandsPanel.add(positionSlider, constraints);

		UIUtils.changeFontRecursive(commandsPanel, new Font(Font.SANS_SERIF, Font.PLAIN, 11));
		add(commandsPanel, BorderLayout.NORTH);
	}

	@Override
	protected void onKeyPressed(KeyEvent e) {
		super.onKeyPressed(e);
		
		if (e.getKeyCode() == KeyEvent.VK_F1 && recordAudioButton.isEnabled())
			recordAudioButton.doClick();
		if (e.getKeyCode() == KeyEvent.VK_F2 && stopButton.isEnabled())
			stopButton.doClick();
		if (e.getKeyCode() == KeyEvent.VK_F3 && playButton.isEnabled())
			playButton.doClick();
		if (e.getKeyCode() == KeyEvent.VK_F4 && pauseButton.isEnabled())
			pauseButton.doClick();
		
		if (e.getKeyCode() == KeyEvent.VK_F5 && stepBackButton.isEnabled())
			stepBackButton.doClick();
		if (e.getKeyCode() == KeyEvent.VK_F6 && rewindButton.isEnabled())
			rewindButton.doClick();
		if (e.getKeyCode() == KeyEvent.VK_F7 && fastForwardButton.isEnabled())
			fastForwardButton.doClick();
		if (e.getKeyCode() == KeyEvent.VK_F8 && stepForwardButton.isEnabled())
			stepForwardButton.doClick();
		
		if (e.getKeyCode() == KeyEvent.VK_F11 && saveButton.isEnabled())
			saveButton.doClick();
		if (e.getKeyCode() == KeyEvent.VK_F12 && cancelButton.isEnabled())
			cancelButton.doClick();
	}
	
	private String millisToTime(int millis) {
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;
		millis -= ((hour * 3600000) + (minute * 60000) + (second * 1000));

		return String.format("%02d:%02d:%02d.%03d", hour, minute, second, millis);
	}
	
	private void closeWindow() {
		JLabel label= new JLabel("¿Qué desea hacer?", SwingConstants.CENTER);
		String[] options = new String[] {"Salir y Desconectar", "Cerrar Ventana", "Cancelar"};
	    int response = 
	    	JOptionPane.showOptionDialog(null, label, "Salir", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		
	    switch(response){
		    case 0: System.exit(0); break;
		    case 1: RecordingForm.this.setVisible(false);
					RecordingForm.this.dispose();
					break;
			default: break;
	    }
	}
	
	public RecordingModel getModel() {
		return model;
	}
}
