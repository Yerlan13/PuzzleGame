import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game extends JFrame {


    private JPanel panel;
    private BufferedImage source;
    private BufferedImage resized;
    private Image image;
    private PuzzleButton lastButton;
    private int wigth, height;

    private ArrayList<Point> solution;
    private ArrayList<PuzzleButton> buttons;

    private final int DESIRED_WIGTH = 600;
    private final int NUMBER_OF_BUTTONS = 12;


    public Game() {
        initUI();

    }

    public void initUI() {
        solution = new ArrayList<>();

        solution.add(new Point(0, 0));
        solution.add(new Point(0, 1));
        solution.add(new Point(0, 2));
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));
        solution.add(new Point(3, 0));
        solution.add(new Point(3, 1));
        solution.add(new Point(3, 2));

        buttons = new ArrayList<>();

        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 3));

        try {
            source = loadImage();
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIGTH, h, BufferedImage.TYPE_INT_ARGB);
        } catch (IOException e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, e);
            System.err.println("Problems with source image" + e);
        }
        wigth = resized.getWidth(null);
        height = resized.getHeight(null);

        add(panel, BorderLayout.CENTER);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(), new CropImageFilter(j * wigth / 3, i * height / 4, wigth / 3, height / 4)));
                PuzzleButton button = new PuzzleButton(image);
                button.putClientProperty("position", new Point(i, j));

                if (i == 3 && j == 2) {
                    lastButton = new PuzzleButton();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton(true);
                    lastButton.putClientProperty("position",new Point(i,j));
                } else {
                    buttons.add(button);
                }
            }
        }


        Collections.shuffle(buttons);
        buttons.add(lastButton);

        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {

            PuzzleButton btn = buttons.get(i);
            panel.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(new ClickAction());

        }
        pack();
        setTitle("Puzzle");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    private BufferedImage resizeImage(BufferedImage originImage, int wigth, int height, int type) {
        BufferedImage resizedImage = new BufferedImage(wigth, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originImage, 0, 0, wigth, height, null);
        g.dispose();
        return resizedImage;
    }

    private BufferedImage loadImage() throws IOException {
        BufferedImage bimg = ImageIO.read(new File("Birds.jpg"));
        return bimg;
    }

    private int getNewHeight(int w, int h) {
        double ratio = DESIRED_WIGTH / (double) w;
        int newHeight = (int) (h * ratio);
        return newHeight;
    }

    private class ClickAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            checkButton(e);
            checkSolution();
        }

        private void checkButton(ActionEvent e) {
            int lidx = 0;

            for (PuzzleButton button : buttons) {
                if (button.isLastButton()) {
                    lidx = buttons.indexOf(button);

                }
            }
            JButton button = (JButton) e.getSource();
            int bidx = buttons.indexOf(button);

            if ((bidx - 1 == lidx)||(bidx + 1 == lidx) || (bidx - 3 == lidx) || bidx + 3 == lidx){

                Collections.swap(buttons, bidx, lidx);
                updateButtons();
            }
        }

        private void updateButtons() {
            panel.removeAll();
            for (JComponent btn : buttons) {
                panel.add(btn);
            }
            panel.validate();
        }

    }

    private void checkSolution() {
        List<Point> current = new ArrayList<>();
        for (JComponent btn : buttons) {
            current.add((Point)btn.getClientProperty("position"));
        }
        if (compareList(solution, current)) {
            JOptionPane.showMessageDialog(panel, "Finished", "Congratulation", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static boolean compareList(List ls1, List ls2) {
        return ls1.toString().contentEquals(ls2.toString());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Game game = new Game();
                game.setVisible(true);
            }
        });
    }
}
