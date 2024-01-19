import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;

public class GameWindow extends JFrame {
    private JFrame gameWindow;

    public Clock blackClock;
    public Clock whiteClock;
    private JPanel whitePanel;
    private JPanel blackPanel;

    private Timer timer;

    private Board board;

    private Clip backgroundMusicClip;

    public GameWindow(String blackName, String whiteName, int hh,
                      int mm, int ss) {
        // Start background music in a separate thread
        new Thread(() -> playBackgroundMusic("music.wav")).start();

        blackClock = new Clock(hh, ss, mm);
        whiteClock = new Clock(hh, ss, mm);

        gameWindow = new JFrame("ChessMaster");
        gameWindow.setLocationRelativeTo(null);
        gameWindow.setResizable(false);

        gameWindow.setBackground(new Color(209, 209, 209));

        gameWindow.setLayout(new BorderLayout(5, 5));
        
        // Game Data window
        JPanel gameData = gameDataPanel(blackName, whiteName, hh, mm, ss);
        gameData.setSize(gameData.getPreferredSize());
        gameWindow.add(gameData, BorderLayout.SOUTH);

        this.board = new Board(this);

        gameWindow.add(board, BorderLayout.CENTER);

        gameWindow.add(buttons(), BorderLayout.NORTH);

        gameWindow.setMinimumSize(gameWindow.getPreferredSize());
        gameWindow.setSize(gameWindow.getPreferredSize());

        gameWindow.pack();
        gameWindow.setVisible(true);
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add a window listener to stop background music when the game window is closed
        gameWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                closeBackgroundMusic();
            }
        });
    }

    // Helper function to create data panel

    private JPanel gameDataPanel(final String bn, final String wn,
            final int hh, final int mm, final int ss) {

	JPanel containerPanel = new JPanel(new GridLayout(1, 2));
	
		// Black Panel
		JPanel blackPanel = new JPanel();
		blackPanel.setBackground(new Color(209, 209, 209));
		blackPanel.setLayout(new GridLayout(3, 1, 0, 0));
		
		JLabel b = new JLabel(bn);
		b.setHorizontalAlignment(JLabel.CENTER);
		b.setVerticalAlignment(JLabel.CENTER);
		b.setSize(b.getMinimumSize());
	
		
		// White Panel
		JPanel whitePanel = new JPanel();
		whitePanel.setBackground(new Color(250, 239, 209));
		whitePanel.setLayout(new GridLayout(3, 1, 0, 0));
		
		JLabel w = new JLabel(wn);
		w.setHorizontalAlignment(JLabel.CENTER);
		w.setVerticalAlignment(JLabel.CENTER);
		w.setSize(w.getMinimumSize());

        // CLOCKS

        final JLabel bTime = new JLabel(blackClock.getTime());
        final JLabel wTime = new JLabel(whiteClock.getTime());

        bTime.setHorizontalAlignment(JLabel.CENTER);
        bTime.setVerticalAlignment(JLabel.CENTER);
        wTime.setHorizontalAlignment(JLabel.CENTER);
        wTime.setVerticalAlignment(JLabel.CENTER);

        if (!(hh == 0 && mm == 0 && ss == 0)) {
            timer = new Timer(1000, null);
            timer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    boolean turn = board.getTurn();

                    if (turn) {
                        whiteClock.decr();
                        wTime.setText(whiteClock.getTime());

                        if (whiteClock.outOfTime()) {
                            timer.stop();
                            int n = JOptionPane.showConfirmDialog(
                                    gameWindow,
                                    bn + " wins by time! Play a new game? \n" +
                                            "Choosing \"No\" quits the game.",
                                    bn + " wins!",
                                    JOptionPane.YES_NO_OPTION);

                            if (n == JOptionPane.YES_OPTION) {
                                new GameWindow(bn, wn, hh, mm, ss);
                                gameWindow.dispose();
                            } else gameWindow.dispose();
                        }
                    } else {
                        blackClock.decr();
                        bTime.setText(blackClock.getTime());

                        if (blackClock.outOfTime()) {
                            timer.stop();
                            int n = JOptionPane.showConfirmDialog(
                                    gameWindow,
                                    wn + " wins by time! Play a new game? \n" +
                                            "Choosing \"No\" quits the game.",
                                    wn + " wins!",
                                    JOptionPane.YES_NO_OPTION);

                            if (n == JOptionPane.YES_OPTION) {
                                new GameWindow(bn, wn, hh, mm, ss);
                                gameWindow.dispose();
                            } else gameWindow.dispose();
                        }
                    }
                }
            });
            timer.start();
        } else {
            wTime.setText("Untimed game");
            bTime.setText("Untimed game");
        }

		wTime.setHorizontalAlignment(JLabel.CENTER);
		wTime.setVerticalAlignment(JLabel.CENTER);
		
		whitePanel.add(w);
		whitePanel.add(wTime);

		bTime.setHorizontalAlignment(JLabel.CENTER);
		bTime.setVerticalAlignment(JLabel.CENTER);
		
		blackPanel.add(b);
		blackPanel.add(bTime);
		containerPanel.add(blackPanel);
		containerPanel.add(whitePanel);
		
		
		
		return containerPanel;
    }

    
    private JPanel buttons() {
        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(209, 209, 209));
        buttons.setLayout(new GridLayout(1, 3, 10, 0));

        final JButton quit = new JButton("Quit");

        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(
                        gameWindow,
                        "Are you sure you want to quit?",
                        "Confirm quit", JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {
                    if (timer != null) timer.stop();
                    gameWindow.dispose();
                }
            }
        });

        final JButton nGame = new JButton("New Game");

        nGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(
                        gameWindow,
                        "Are you sure you want to start a new game?",
                        "Confirm new game", JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {
                    SwingUtilities.invokeLater(new StartMenu());
                    gameWindow.dispose();
                }
            }
        });

        final JButton instr = new JButton("Help");

        instr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(gameWindow,
                        "Move the chess pieces on the board by clicking\n"
                                + "and dragging. The game will watch out for illegal\n"
                                + "moves. You can win either by your opponent running\n"
                                + "out of time or by checkmating your opponent.\n"
                                + "\nGood luck, hope you enjoy the game!",
                        "How to play",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

        final JButton settings = new JButton("Preferences");

        settings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open the settings panel here
                openSettingsPanel();
            }
        });

        buttons.add(instr);
        buttons.add(nGame);
        buttons.add(settings);
        buttons.add(quit);

        buttons.setPreferredSize(buttons.getMinimumSize());

        return buttons;
    }
    
    private void openSettingsPanel() {
        // Create a JPanel for the settings components
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(2, 1));

        // Checkbox to toggle music
        JCheckBox musicCheckbox = new JCheckBox("Enable Music");
        musicCheckbox.setSelected(Board.sfx); // Set initial state based on sfx status

        // Add an ActionListener to the checkbox
        musicCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (musicCheckbox.isSelected()) {
                    // Start music
                    closeBackgroundMusic();
                    new Thread(() -> playBackgroundMusic("music.wav")).start();
                } else {
                    // Stop music
                    closeBackgroundMusic();
                }
            }
        });

        // Checkbox to toggle sound effects
        JCheckBox sfxCheckbox = new JCheckBox("Enable Sound Effects");
        sfxCheckbox.setSelected(Board.sfx); // Set initial state based on sound effects status

        // Add an ActionListener to the sfx checkbox
        sfxCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Board.sfx = sfxCheckbox.isSelected();
            }
        });

        // Add components to the settings panel
        settingsPanel.add(musicCheckbox);
        settingsPanel.add(sfxCheckbox);

        // Create a JOptionPane to show the settings panel
        JOptionPane.showMessageDialog(gameWindow, settingsPanel, "Settings", JOptionPane.PLAIN_MESSAGE);
    }


    public void checkmateOccurred(int c) {
        if (c == 0) {
            if (timer != null) timer.stop();
            int n = JOptionPane.showConfirmDialog(
                    gameWindow,
                    "White wins by checkmate! \nNew Game? \n" +
                            "",
                    "White wins!",
                    JOptionPane.YES_NO_OPTION);

            if (n == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(new StartMenu());
                gameWindow.dispose();
            }
        } else {
            if (timer != null) timer.stop();
            int n = JOptionPane.showConfirmDialog(
                    gameWindow,
                    "Black wins by checkmate! \nNew Game? \n" +
                            "",
                    "Black wins!",
                    JOptionPane.YES_NO_OPTION);

            if (n == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(new StartMenu());
                gameWindow.dispose();
            }
        }
    }

    private void playBackgroundMusic(String filePath) {
        try {
            // Open the audio input stream
            InputStream audioInputStream = getClass().getResourceAsStream(filePath);
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(audioInputStream));

            // Create a DataLine.Info object for Clip playback
            DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());

            // Check if the system supports the DataLine.Info object
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                return;
            }

            // Obtain and open the Clip
            backgroundMusicClip = (Clip) AudioSystem.getLine(info);
            backgroundMusicClip.open(ais);

            // Start playing the clip in a loop
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void closeBackgroundMusic() {
        if (backgroundMusicClip != null) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
        }
    }
}
