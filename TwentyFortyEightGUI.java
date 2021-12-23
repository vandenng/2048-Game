package Project2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/*************************************************************************
 * 2048 GUI!
 * 
 * @author Nathan VandenHoek
 * @version 1.0
 *
 *************************************************************************/
public class TwentyFortyEightGUI extends JFrame implements ActionListener, KeyListener {
	private JMenuBar menus;
	private JMenu fileMenu;
	private JMenuItem quitItem;
	private JMenuItem clearItem, resize;
	private JPanel board, stats;
	private JLabel[][] labels, tempLabels;
	private ImageIcon image;
	private NumberGame game;
	private int height, width, wins;
	private GridBagConstraints position;
	private Color gray;
	private String input;
	private JLabel title, move, highestScore, gamePlay, numOfWins;
	private Font myFont;
	private int moves, highScore, gamesPlayed, numberOfWins;
	private String heights[] = {"2", "3", "4", "5", "6", "7", "8"};
	private String widths[] = {"2", "3", "4", "5", "6", "7", "8"};
	private String winingValues[] = { "16", "32", "64", "128", "256", "512", "1024", "2048", "4096", "8192" };
	private JComboBox heightBox, columnBox, winVal;
	private JPanel EntryBox;
	private JLabel h, w, win;

	/**************************************************************
	 * The main method will set up the JFrame and set it visible.
	 * @param args
	 *************************************************************/
	public static void main(String[] args) {
		TwentyFortyEightGUI gui = new TwentyFortyEightGUI();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setTitle("2048");
		gui.setBounds(0, 0, 800, 800);
		gui.setResizable(true);
		gui.pack();
		gui.setVisible(true);
	}

	/****************************************************************
	 * Set up the main board, and the layout of the JFrame. Uses
	 * Border layout for the main grid, and GridBagLayout for the
	 * 2048 board.
	 ****************************************************************/
	public TwentyFortyEightGUI() {
		setLayout(new BorderLayout());
		this.setupMenus();
		this.moves = 0;
		this.gamesPlayed = 0;
		this.numberOfWins = 0;
		this.EntryBox = new JPanel();
		this.game = new NumberGame();
		this.getBoardSize();
		this.game.resizeBoard(this.height, this.width, this.wins);
		this.labels = new JLabel[8][8];

		this.board = new JPanel(new GridBagLayout());
		this.setBoardUp();
		add(this.board, BorderLayout.CENTER);

		this.board.addKeyListener(this);
		this.board.setFocusable(true);

		this.gray = Color.decode("#BBADA0");
		this.board.setBackground(this.gray);

		this.title = new JLabel("2048");
		this.myFont = new Font(Font.SANS_SERIF, Font.BOLD, 42);
		this.title.setFont(this.myFont);
		this.title.setForeground(Color.gray);
		add(this.title, BorderLayout.NORTH);

		this.stats = new JPanel(new BorderLayout());

		this.move = new JLabel();
		this.stats.add(this.move, BorderLayout.SOUTH);

		this.highestScore = new JLabel();
		this.stats.add(this.highestScore, BorderLayout.NORTH);

		this.gamePlay = new JLabel();
		this.stats.add(this.gamePlay, BorderLayout.CENTER);

		this.numOfWins = new JLabel();
		this.stats.add(this.numOfWins, BorderLayout.EAST);

		add(this.stats, BorderLayout.SOUTH);
		this.restart();
	}

	/************************************************************************
	 * Set up the board by setting the labels equal to the background image.
	 ************************************************************************/
	private void setBoardUp() {
		this.position = new GridBagConstraints();

		for (int i = 0; i < this.labels.length; i++) {
			this.position.gridy = i;
			for (int j = 0; j < this.labels[0].length; j++) {
				if (i < this.height && j < this.width) {
					this.position.gridx = j;
					this.position.insets = new Insets(10, 10, 10, 10);
					this.labels[i][j] = new JLabel("", this.getImage(0), JLabel.CENTER);
					this.board.add(this.labels[i][j], this.position);
				} else {
					this.labels[i][j] = new JLabel("", null, JLabel.CENTER);
				}
			}
		}
	}

	/*********************************************************************
	 * This method keeps track moves, games played, high score, and
	 * number of wins for the user.
	 ********************************************************************/
	private void liveStats() {
		this.move.setText("Moves: " + moves);
		this.gamePlay.setText("Games Played: " + this.gamesPlayed);
		this.highestScore.setText("Highest Score: " + this.highScore);
		this.numOfWins.setText("Number of Wins: " + this.numberOfWins);
	}

	/*********************************************************************
	 * Get the height, width, and winning value from
	 * the user.
	 ********************************************************************/
	private void getBoardSize() {
		this.heightBox = new JComboBox(this.heights);
		this.heightBox.setEditable(false);
		this.heightBox.addActionListener(this);
		this.heightBox.setSelectedIndex(2);
		this.heightBox.setMaximumRowCount(8);
		this.h = new JLabel("Height: ");
		this.EntryBox.add(h);
		this.EntryBox.add(heightBox);
		this.EntryBox.add(Box.createHorizontalStrut(15));

		this.columnBox = new JComboBox(this.widths);
		this.columnBox.setEditable(false);
		this.columnBox.addActionListener(this);
		this.columnBox.setSelectedIndex(2);
		this.columnBox.setMaximumRowCount(8);
		this.w = new JLabel("Width: ");
		this.EntryBox.add(w);
		this.EntryBox.add(columnBox);
		this.EntryBox.add(Box.createHorizontalStrut(15));

		this.winVal = new JComboBox(this.winingValues);
		this.winVal.setEditable(false);
		this.winVal.addActionListener(this);
		this.winVal.setSelectedIndex(7);
		this.winVal.setMaximumRowCount(10);
		this.win = new JLabel("Winning Value: ");
		this.EntryBox.add(win);
		this.EntryBox.add(winVal);
		
		
		JOptionPane.showMessageDialog(null, this.EntryBox, "Enter Board Dimensions: ", JOptionPane.QUESTION_MESSAGE);
		this.input = (String) this.heightBox.getSelectedItem();
		this.height = Integer.parseInt(this.input);
		this.input = (String) this.columnBox.getSelectedItem();
		this.width = Integer.parseInt(this.input);
		this.input = (String) this.winVal.getSelectedItem();
		this.wins = Integer.parseInt(this.input);
		this.EntryBox.removeAll();
	}

	/*********************************************************************
	 * Set up the background to the game board.
	 ********************************************************************/
	private void help() {
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				this.labels[i][j].setIcon(this.getImage(0));
			}
		}
	}

	/*********************************************************************
	 * Add the numbered tiles to the board.
	 ********************************************************************/
	private void renderBoard() {
		this.help();
		for (Cell c : game.getNonEmptyTiles()) {
			this.labels[c.row][c.column].setIcon(this.getImage(c.value));
			this.getHighScore(c.value);
		}
		this.liveStats();
		this.isGameOver();
	}

	/*********************************************************************
	 * Get the highest ranking tile on the board.
	 * 
	 * @param val
	 * The value of the highest tile on the board.
	 ********************************************************************/
	private void getHighScore(int val) {
		if (val > this.highScore) {
			this.highScore = val;
		}
		this.liveStats();
	}

	/*********************************************************************
	 * Check if the game is over by implementing the game logic.
	 * Then restart the game.
	 *********************************************************************/
	private void isGameOver() {
		if (game.getStatus() == GameStatus.USER_WON) {
			JOptionPane.showMessageDialog(this, "YOU WON!");
			this.numberOfWins++;
			this.gamesPlayed++;
			this.restart();
		}

		if (game.getStatus() == GameStatus.USER_LOST) {
			JOptionPane.showMessageDialog(this, "YOU LOST!");
			this.gamesPlayed++;
			this.restart();
		}
	}

	/*********************************************************************
	 * This method takes an value and gets the corresponding image. The
	 * background image is set to the value zero.
	 * 
	 * @param val
	 * 	The number needed to be shown on the tile.
	 * @return 
	 * 	The image the corresponds to the value given.
	 ********************************************************************/
	private ImageIcon getImage(int val) {
		switch (val) {
		case 0:
			this.image = new ImageIcon("F:/Cis 163/Projects/Project 2/Background.png");
			break;
		case 2:
			this.image = new ImageIcon("F:/Cis 163/Projects/Project 2/2.png");
			break;
		case 4:
			this.image = new ImageIcon("F:/Cis 163/Projects/Project 2/4.png");
			break;
		case 8:
			this.image = new ImageIcon("F:/Cis 163/Projects/Project 2/8.png");
			break;
		case 16:
			this.image = new ImageIcon("F:/Cis 163/Projects/Project 2/16.png");
			break;
		case 32:
			this.image = new ImageIcon("F:/Cis 163/Projects/Project 2/32.png");
			break;
		case 64:
			this.image = new ImageIcon("F:/Cis 163/Projects/Project 2/64.png");
			break;
		case 128:
			this.image = new ImageIcon("F:/Cis 163/Projects/Project 2/128.png");
			break;
		case 256:
			this.image = new ImageIcon("F:/Cis 163/Projects/Project 2/256.png");
			break;
		case 512:
			this.image = new ImageIcon("F:/Cis 163/Projects/Project 2/512.png");
			break;
		case 1024:
			this.image = new ImageIcon("F:/Cis 163/Projects/Project 2/1024.png");
			break;
		case 2048:
			this.image = new ImageIcon("F:/Cis 163/Projects/Project 2/2048.png");
			break;
		default:
			throw new IllegalArgumentException();
		}
		return this.image;
	}

	/*********************************************************************
	 *  Use the keyboard to move the board up, down, left, right by using
	 *  W, S, A, D. Then implement the moves and render the board.
	 ********************************************************************/
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_W) {
			if (game.slide(SlideDirection.UP)) {
				this.moves++;
				this.renderBoard();
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (game.slide(SlideDirection.UP)) {
				this.moves++;
				this.renderBoard();
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			if (game.slide(SlideDirection.DOWN)) {
				this.moves++;
				this.renderBoard();
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (game.slide(SlideDirection.DOWN)) {
				this.moves++;
				this.renderBoard();
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_D) {
			if (game.slide(SlideDirection.RIGHT)) {
				this.moves++;
				this.renderBoard();
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (game.slide(SlideDirection.RIGHT)) {
				this.moves++;
				this.renderBoard();
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_A) {
			if (game.slide(SlideDirection.LEFT)) {
				this.moves++;
				this.renderBoard();
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (game.slide(SlideDirection.LEFT)) {
				this.moves++;
				this.renderBoard();
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_U) {
			try {
				game.undo();
				this.moves++;
				this.renderBoard();
			} catch (IllegalStateException ex) {
				JOptionPane.showMessageDialog(this, "Can't undo that far!");
			}
		}
	}

	/*********************************************************************
	 * The reaction to the user pressing the buttons on the 
	 * JMenu bar.
	 ********************************************************************/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.quitItem) {
			System.exit(1);
		}

		else if (e.getSource() == this.clearItem) {
			this.gamesPlayed++;
			this.restart();
		}

		else if (e.getSource() == this.resize) {
			this.gamesPlayed++;
			this.changeSize();
		}
	}

	/*********************************************************************
	 * Change the size of the game board without exiting the 
	 * game board. Each Image is 90 by 90 pixels with 10 pixes of padding
	 * on all sides.
	 ********************************************************************/
	public void changeSize() {
		int oldHeight = this.height, oldWidth = this.width;
		int newH = 0, newW = 0;

		this.getBoardSize();
		game.resizeBoard(this.height, this.width, this.wins);
		if (this.height < oldHeight) {
			newH = oldHeight;
		} else {
			newH = this.height;
		}

		if (this.width < oldWidth) {
			newW = oldWidth;
		} else {
			newW = this.width;
		}

		this.tempLabels = new JLabel[newH][newW];
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				this.tempLabels[i][j] = new JLabel(this.getImage(0));
			}
		}

		if (this.height < oldHeight || this.width < oldWidth) {
			for (int i = 0; i < oldHeight; i++) {
				for (int j = 0; j < oldWidth; j++) {
					if (this.tempLabels[i][j] == null) {
						this.board.remove(this.labels[i][j]);
					}
				}
			}
		}

		if (this.height > oldHeight || this.width > oldWidth) {
			this.position = new GridBagConstraints();
			for (int i = 0; i < this.height; i++) {
				this.position.gridy = i;
				for (int j = 0; j < this.width; j++) {
					if (i >= oldHeight || j >= oldWidth) {
						this.position.gridx = j;
						this.position.insets = new Insets(10, 10, 10, 10);
						this.board.add(this.labels[i][j], this.position);
					}
				}
			}
		}

		System.arraycopy(this.labels, 0, this.tempLabels, 0, this.height);
		//Set the preferred size to the image size and the insets times
		//the amoun in each row and column.
		this.setPreferredSize(new Dimension((this.width * 110) + 18, (this.height * 110) + 172));
		this.pack();
		this.restart();
	}

	/*********************************************************************
	 * Restart the game logic and the game labels.
	 ********************************************************************/
	private void restart() {
		game.reset();
		this.moves = 0;
		this.renderBoard();
	}

	/*********************************************************************
	 * Set up the JMenue and add action listeners to them.
	 ********************************************************************/
	private void setupMenus() {

		// create menu components
		this.fileMenu = new JMenu("Options");
		this.quitItem = new JMenuItem("Exit");
		this.clearItem = new JMenuItem("Reset");
		this.resize = new JMenuItem("Resize");

		// assign action listeners
		this.quitItem.addActionListener(this);
		this.clearItem.addActionListener(this);
		this.resize.addActionListener(this);

		// display menu components
		this.fileMenu.add(this.resize);
		this.fileMenu.add(this.clearItem);
		this.fileMenu.add(this.quitItem);
		this.menus = new JMenuBar();

		this.menus.add(this.fileMenu);
		setJMenuBar(this.menus);
	}

	public void keyTyped(KeyEvent e) {
		
	}

	public void keyReleased(KeyEvent e) {

	}
}
