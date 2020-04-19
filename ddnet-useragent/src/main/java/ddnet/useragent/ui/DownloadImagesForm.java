package ddnet.useragent.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import ddnet.useragent.App;
import ddnet.useragent.CommandHandler;
import ddnet.useragent.User;
import ddnet.useragent.UserAgentEventHandler;
import ddnet.useragent.commands.CommandEvent;
import ddnet.useragent.downloads.Download;
import ddnet.useragent.downloads.DownloadEventHandler;
import ddnet.useragent.util.ClipboardUtils;

public class DownloadImagesForm extends Form {
	private static final Logger log = Logger.getLogger(DownloadImagesForm.class);
	
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "DDNET UserAgent v" + App.VERSION;
	private static final int DOWNLOAD_TABLE_MODEL_ICON_INDEX = 0;
	private static final int DOWNLOAD_TABLE_MODEL_DOWNLOADID_INDEX = 1;
	private static final int DOWNLOAD_TABLE_MODEL_STATUS_INDEX = 4;
	private static final int DOWNLOAD_TABLE_MODEL_STATUS_DETAILS_INDEX = 5;
	
	private static final String ACTION_COMMAND_EXIT_APP = "exit";
	private static final String ACTION_COMMAND_CLEAN_FINISHED_DOWNLOADS = "clean-finished-downloads";
	private static final String ACTION_COMMAND_CANCEL_DOWNLOADS = "cancel-downloads";
	private static final String ACTION_COMMAND_RUN_ON_DOWNLOAD_COMPLETED_ACTION = "run-download-complete-action";
	private static final String ACTION_COMMAND_SEND_APP_LOGS = "send-app-log";
	
	private final ImageIcon okIcon;
	private final ImageIcon warningIcon;
	private final ImageIcon errorIcon;
	private final ImageIcon downloadingIcon;
	private final ImageIcon canceledIcon;
    private final DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();            
	
	private final DownloadImagesModel model;
	private DefaultTableModel downloadsTableModel;
	private static Object downloadsTableModelSyncObject = new Object();
	private JTable downloadsTable;
	private JLabel statusMessagesLabel;
	private JMenuBar mainMenuBar;
	private final ActionListener formActionListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			switch(e.getActionCommand()) {
				case ACTION_COMMAND_EXIT_APP: {
					App.getInstance().shutdown();
					break;
				}
				case ACTION_COMMAND_RUN_ON_DOWNLOAD_COMPLETED_ACTION: {
					runOnDownloadCompletedAction();
					break;
				}
				case ACTION_COMMAND_CLEAN_FINISHED_DOWNLOADS: {
					clearFinishedDownloads();
					break;
				}
				case ACTION_COMMAND_SEND_APP_LOGS: {
					sendAppLogs();
					break;
				}
				case ACTION_COMMAND_CANCEL_DOWNLOADS: {
					cancelDownloads();
					break;
				}
			}			
		}
	};
	
	public DownloadImagesForm(DownloadImagesModel model) {
		if (model == null)
			throw new IllegalArgumentException("model");
		
		this.model = model;
		
		this.okIcon = new ImageIcon(getClass().getResource("/images/ok.png"));
		this.warningIcon = new ImageIcon(getClass().getResource("/images/warning.png"));
		this.errorIcon = new ImageIcon(getClass().getResource("/images/error.png"));
		this.downloadingIcon = new ImageIcon(getClass().getResource("/images/downloading.png"));
		this.canceledIcon = new ImageIcon(getClass().getResource("/images/canceled.png"));
		this.rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		
		configureForm();
		registerEventsHandler();
	}
	
	private void registerEventsHandler() {        
        App.getInstance().getUserAgent().addEventHandler(new UserAgentEventHandler() {			
			@Override
			public void onCurrentUserChanged(User newCurrentUser) {
				setTitle(newCurrentUser);
			}
			@Override
			public void onCommandEvent(CommandEvent event) {
				if (!event.getCommand().getProperties().containsKey(CommandHandler.DOWNLOAD_PROPERTY))
					return;
				
				Download download = (Download)event.getCommand().getProperties().get(CommandHandler.DOWNLOAD_PROPERTY);
				if (download == null)
					return;
				
				updateDownloadActionInfo(download, 
						event.isError() ? errorIcon : event.isWarn() ? warningIcon : okIcon, 
						event.getText(), event.getDetails());
			}
		});
        App.getInstance().getUserAgent().getDownloadManager().addEventHandler(new DownloadEventHandler() {        	
			@Override
			public void onNewDownload(Download download) {
				synchronized (downloadsTableModelSyncObject) {
					downloadsTableModel.addRow(new Object[] {
							downloadingIcon,
							download.getID(), 
							download.getDescription(), 
							download.getType(),
							String.format("%s  ", download.getStatusText())
					});
				}
			}

			@Override
			public void onUpdate(Download download) {
				updateDownloadStatus(download, downloadingIcon);
			}			
			
			@Override
			public void onCompleted(Download download) {
				updateDownloadStatus(download, okIcon);	
			}
			
			@Override
			public void onFinishedWithErrors(Download download) {
				updateDownloadStatus(download, errorIcon);
			}

			@Override
			public void onCanceled(Download download) {
				updateDownloadStatus(download, canceledIcon);
			}			
		});
	}

	public DownloadImagesModel getModel() {
		return model;
	}
		
	private void configureForm() {
		setSize(800, 240);
		setTitle(App.getInstance().getUserAgent().getCurrentUser());
		setLocationRelativeTo(null); // Centra el form en la pantalla.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);		
		addWindowListener(new WindowAdapter() {
			private boolean maximized = false;
			private boolean flagSaved = false;
			
			@Override
			public void windowClosing(WindowEvent e) {
				if (!flagSaved) {
					maximized = (DownloadImagesForm.this.getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH;
					flagSaved = true;
				}
				DownloadImagesForm.this.setExtendedState(ICONIFIED);
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				if (!flagSaved) {
					maximized = (DownloadImagesForm.this.getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH;
					flagSaved = true;
				}
				DownloadImagesForm.this.setExtendedState(ICONIFIED);
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {				
				flagSaved = false;
				DownloadImagesForm.this.setExtendedState(maximized ? MAXIMIZED_BOTH : NORMAL);
			}
		});
        setLayout(new BorderLayout());
        
        // Menu principal
        JMenu topMenu = null;
        JMenuItem menuItem = null;
        mainMenuBar = new JMenuBar();
 
        // Archivo
        topMenu = new JMenu("Archivo");
        topMenu.setMnemonic(KeyEvent.VK_A);
        mainMenuBar.add(topMenu);
        
        menuItem = new JMenuItem("Salir");
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.setActionCommand(ACTION_COMMAND_EXIT_APP);
        menuItem.addActionListener(formActionListener);
        topMenu.add(menuItem);
 
        // Descargas
        topMenu = new JMenu("Descargas");
        topMenu.setMnemonic(KeyEvent.VK_D);
        mainMenuBar.add(topMenu);

        menuItem = new JMenuItem("Cancelar");
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.setActionCommand(ACTION_COMMAND_CANCEL_DOWNLOADS);
        menuItem.addActionListener(formActionListener);
        topMenu.add(menuItem);
        
        menuItem = new JMenuItem("Ejecutar acción post-descarga");
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.setActionCommand(ACTION_COMMAND_RUN_ON_DOWNLOAD_COMPLETED_ACTION);
        menuItem.addActionListener(formActionListener);
        topMenu.add(menuItem);
        
        menuItem = new JMenuItem("Limpiar");
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.setActionCommand(ACTION_COMMAND_CLEAN_FINISHED_DOWNLOADS);
        menuItem.addActionListener(formActionListener);
        topMenu.add(menuItem);
         
        // Herramientas
        topMenu = new JMenu("Herramientas");
        topMenu.setMnemonic(KeyEvent.VK_H);
        mainMenuBar.add(topMenu);
        
        menuItem = new JMenuItem("Enviar logs de aplicación");
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.setActionCommand(ACTION_COMMAND_SEND_APP_LOGS);
        menuItem.addActionListener(formActionListener);
        topMenu.add(menuItem);
        
        this.setJMenuBar(mainMenuBar);
        
        // Tabla de estudios a descargar.
        downloadsTableModel = new DefaultTableModel(new String[] { " ", "ID", "DESCRIPCIÓN", "TIPO", "PROGRESO", "DETALLE" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
        };
        
        downloadsTable = new JTable(downloadsTableModel) {
			private static final long serialVersionUID = 1L;
			
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }		
            
            @Override
            public String getToolTipText(MouseEvent event) {
            	String tip = null;
                java.awt.Point p = event.getPoint();
                int rowIndex = rowAtPoint(p);
                //int colIndex = columnAtPoint(p);

                try {
                    tip = downloadsTableModel.getValueAt(rowIndex, DOWNLOAD_TABLE_MODEL_STATUS_DETAILS_INDEX).toString();
                } catch (Throwable ignore) { }

                return tip;
             }
        };
        
        downloadsTable.setCellSelectionEnabled(false);
        downloadsTable.setRowSelectionAllowed(true);
        downloadsTable.setRowHeight(28);
        
        downloadsTable.getColumnModel().removeColumn(downloadsTable.getColumnModel().getColumn(1));
        downloadsTable.getColumnModel().removeColumn(downloadsTable.getColumnModel().getColumn(4));
        downloadsTable.getColumnModel().getColumn(0).setMinWidth(28);
        downloadsTable.getColumnModel().getColumn(0).setMaxWidth(28);
        downloadsTable.getColumnModel().getColumn(0).setResizable(false);
        downloadsTable.getColumnModel().getColumn(1).setPreferredWidth(10000);
        downloadsTable.getColumnModel().getColumn(1).setResizable(false);
        downloadsTable.getColumnModel().getColumn(2).setMinWidth(80);
        downloadsTable.getColumnModel().getColumn(2).setMaxWidth(80);
        downloadsTable.getColumnModel().getColumn(2).setResizable(false);
        downloadsTable.getColumnModel().getColumn(3).setMinWidth(330);
        downloadsTable.getColumnModel().getColumn(3).setMaxWidth(330);
        downloadsTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        downloadsTable.getColumnModel().getColumn(3).setResizable(false);
        downloadsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane tableScrollPane = new JScrollPane(downloadsTable);
        downloadsTable.setFillsViewportHeight(true);        
		add(tableScrollPane, BorderLayout.CENTER);
        
		// Mensajes de estado.
		JPanel bottomPanel = new JPanel(new BorderLayout());
		statusMessagesLabel = new JLabel("");
		bottomPanel.add(statusMessagesLabel, BorderLayout.WEST);

		JPanel rightBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		// "Cancelar"
		JButton cancelDownloadsButton = new JButton("Cancelar");
		cancelDownloadsButton.setActionCommand(ACTION_COMMAND_CANCEL_DOWNLOADS);
		cancelDownloadsButton.addActionListener(formActionListener);
		rightBottomPanel.add(cancelDownloadsButton);
		
		// "Limpiar"
		JButton clearFinishedDownloadsButton = new JButton("Limpiar");
		clearFinishedDownloadsButton.setActionCommand(ACTION_COMMAND_CLEAN_FINISHED_DOWNLOADS);
		clearFinishedDownloadsButton.addActionListener(formActionListener);
		rightBottomPanel.add(clearFinishedDownloadsButton);
		
		// "Salir"
		JButton exitButton = new JButton("Salir");
		exitButton.setActionCommand(ACTION_COMMAND_EXIT_APP);
		exitButton.addActionListener(formActionListener);
		rightBottomPanel.add(exitButton);
		
		bottomPanel.add(rightBottomPanel, BorderLayout.EAST);
		add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void runOnDownloadCompletedAction() {
		try {
			int[] selectedRows = downloadsTable.getSelectedRows();
			if (selectedRows.length == 0) {
				JOptionPane.showMessageDialog(null, "No se han seleccionada descargas.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			for(int selectedRow : selectedRows) {	
				synchronized (downloadsTableModelSyncObject) {
					final UUID downloadID = (UUID)downloadsTableModel.getValueAt(selectedRow, DOWNLOAD_TABLE_MODEL_DOWNLOADID_INDEX);
					final Download download = App.getInstance().getUserAgent().getDownloadManager().getDownload(downloadID);
					if (download != null) {
						if (!download.isFinalized()) {
							String message = String.format("La descarga '%s' todavía se encuentra en curso. \nCuando la misma finalice, "
									+ "la acción post-descarga será ejecutada automáticamente.", download.getDescription());
							JOptionPane.showMessageDialog(null, message, "Aviso", JOptionPane.INFORMATION_MESSAGE);
						} else if (!download.isFullyDownloaded()) {
							String message = String.format("La descarga '%s' finalizó pero no se descargó la totalidad del contenido.\n"
									+ "¿Desea reintentar la descarga de datos? La acción post-descarga será ejecutada automáticamente a continuación.", 
									download.getDescription());
							if (JOptionPane.showConfirmDialog(null, message, "Aviso", JOptionPane.YES_NO_OPTION, 
									JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
								App.getInstance().getUserAgent().getDownloadManager().restartDownload(downloadID);
							}							
						} else {
							App.getInstance().getUserAgent().getDownloadManager().reRunOnDownloadCompletedAction(downloadID);
						}
					}
				}
			}
		} catch(Throwable t) {
			JOptionPane.showMessageDialog(null, "Ha ocurrido un error intentando ejecutar las operaciones post-descarga.", "Error", 
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void clearFinishedDownloads() {
		try {
			synchronized (downloadsTableModelSyncObject) {
				List<Integer> rowsToRemove = new ArrayList<Integer>();
				for(int index=0; index<downloadsTableModel.getRowCount(); index++) {
					final UUID downloadID = (UUID)downloadsTableModel.getValueAt(index, DOWNLOAD_TABLE_MODEL_DOWNLOADID_INDEX);
					final Download download = App.getInstance().getUserAgent().getDownloadManager().getDownload(downloadID);
					if (download == null) {
						rowsToRemove.add(index);
					} else if (download.isFinalized()) {
						rowsToRemove.add(index);
						App.getInstance().getUserAgent().getDownloadManager().removeDownload(downloadID);
					}
				}
				
				for(int index=rowsToRemove.size()-1; index>=0; index--)
					downloadsTableModel.removeRow(rowsToRemove.get(index));
			}	
		} catch(Throwable t) {
			log.error("Error limpiando descargas completas", t);
		}
	}
	
	private void cancelDownloads() {
		try {
			int[] selectedRows = downloadsTable.getSelectedRows();
			if (selectedRows.length == 0) {
				JOptionPane.showMessageDialog(null, "No se han seleccionada descargas.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			for(int selectedRow : selectedRows) {	
				synchronized (downloadsTableModelSyncObject) {
					final UUID downloadID = (UUID)downloadsTableModel.getValueAt(selectedRow, DOWNLOAD_TABLE_MODEL_DOWNLOADID_INDEX);
					final Download download = App.getInstance().getUserAgent().getDownloadManager().getDownload(downloadID);
					if (download != null)
						download.setCanceled(true);
				}
			}
		} catch(Throwable t) {
			JOptionPane.showMessageDialog(null, "Ha ocurrido un error intentando cancelar las descargas seleccionadas.", "Error", 
					JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	private void sendAppLogs() {
		final String clipboardContent = App.getInstance().getLogFile().getAbsolutePath();
		ClipboardUtils.setClipboardContent(clipboardContent);
		
		JOptionPane.showMessageDialog(null, 
				String.format("Se ha copiado al portapapeles la ubicación completa del archivo de diagnóstico de la\n"
				+ "aplicación. Por favor, envíe  el mismo por los medios  habituales (correo electrónico,\n"
				+ "por ejemplo) al contacto de soporte técnico de Diagnóstico Digital.\n\n"
				+ "¡Muchas gracias!"));	
	}

	private void setTitle(User currentUser) {
		if (currentUser == null)
			setTitle(TITLE);
		else
			setTitle(String.format("%s - Usuario activo: %s [%s]", TITLE, currentUser.getLogin(), currentUser.getFullName()));
	}

	private void updateDownloadStatus(Download download, ImageIcon icon) {
		synchronized (downloadsTableModelSyncObject) {
			for(int index=0; index<downloadsTableModel.getRowCount(); index++) {
				if (downloadsTableModel.getValueAt(index, DOWNLOAD_TABLE_MODEL_DOWNLOADID_INDEX).equals(download.getID())) {
					downloadsTableModel.setValueAt(icon, index, DOWNLOAD_TABLE_MODEL_ICON_INDEX);
					downloadsTableModel.setValueAt(String.format("%s  ", download.getStatusText()), index, DOWNLOAD_TABLE_MODEL_STATUS_INDEX);
					break;
				}
			}
		}
	}			

	private void updateDownloadActionInfo(Download download, ImageIcon icon, String actionInfo, String details) {
		synchronized (downloadsTableModelSyncObject) {
			for(int index=0; index<downloadsTableModel.getRowCount(); index++) {
				if (downloadsTableModel.getValueAt(index, DOWNLOAD_TABLE_MODEL_DOWNLOADID_INDEX).equals(download.getID())) {
					downloadsTableModel.setValueAt(icon, index, DOWNLOAD_TABLE_MODEL_ICON_INDEX);
					downloadsTableModel.setValueAt(String.format("%s  ", actionInfo), index, DOWNLOAD_TABLE_MODEL_STATUS_INDEX);
					downloadsTableModel.setValueAt(details, index, DOWNLOAD_TABLE_MODEL_STATUS_DETAILS_INDEX);
					break;
				}
			}
		}
	}	
}
