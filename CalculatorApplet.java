import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

/**
 * CalculatorApplet
 * 
 * A fully functional, modern-styled calculator implemented as a Java Swing Applet
 * (modern UI inspired by Apple style and material design concept, adapted for Swing)
 * 
 * Features:
 * - Header with brand identity (glass morphism simulated)
 * - Main calculator interface with display and buttons
 * - History display for calculation results
 * - Footer with status and info
 * - Support for basic operations: +, -, *, /, %, ., C (clear), =
 * - Keyboard support for digits and operations
 * - Responsive layout with proper spacing and alignment
 */
public class CalculatorApplet extends JApplet {

    private JTextField displayField;
    private JTextArea historyArea;
    private JLabel statusLabel;

    // Calculator state
    private double lastResult = 0;
    private String lastOperator = "";
    private boolean startNewNumber = true;

    private DecimalFormat decimalFormat = new DecimalFormat("##########.##########");

    @Override
    public void init() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createAndShowGUI();
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to initialize applet.");
        }
    }

    /**
     * Build all UI components and layout
     */
    private void createAndShowGUI() {
        // Use BorderLayout as main
        setLayout(new BorderLayout());

        // Add header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Add main calculator panel
        add(createCalculatorPanel(), BorderLayout.CENTER);

        // Add history panel
        add(createHistoryPanel(), BorderLayout.EAST);

        // Add footer
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Set preferred size for applet area (for testing)
        setPreferredSize(new Dimension(800, 400));
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setPreferredSize(new Dimension(0, 64));
        header.setBackground(new Color(255, 255, 255, 180)); // translucent white simulating glassmorphism
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200, 120)));

        // Brand label
        JLabel brandLabel = new JLabel("Calculator Pro");
        brandLabel.setFont(new Font("Inter", Font.BOLD, 24));
        brandLabel.setForeground(new Color(50, 50, 50));
        brandLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        header.add(brandLabel, BorderLayout.WEST);

        return header;
    }

    /**
     * Create main calculator panel with display and buttons
     */
    private JPanel createCalculatorPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Display field
        displayField = new JTextField("0");
        displayField.setFont(new Font("Inter", Font.BOLD, 36));
        displayField.setHorizontalAlignment(SwingConstants.RIGHT);
        displayField.setEditable(false);
        displayField.setBackground(new Color(250, 250, 250));
        displayField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        mainPanel.add(displayField, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(5, 4, 12, 12));
        buttonsPanel.setOpaque(false);

        String[] buttons = {
            "C", "%", "/", "*",
            "7", "8", "9", "-",
            "4", "5", "6", "+",
            "1", "2", "3", "=",
            "0", "."
        };

        for (String text : buttons) {
            buttonsPanel.add(createCalcButton(text));
        }
        mainPanel.add(buttonsPanel, BorderLayout.CENTER);

        // Keyboard support
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(new KeyEventDispatcher() {
                public boolean dispatchKeyEvent(KeyEvent e) {
                    return handleKeyEvent(e);
                }
            });

        return mainPanel;
    }

    /**
     * Create a history panel to display previous calculations
     */
    private JPanel createHistoryPanel() {
        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BorderLayout());
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Inter", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        return historyPanel;
    }

    private JButton createCalcButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Inter", Font.BOLD, 24));
        button.setFocusPainted(false);
        button.setBackground(new Color(240, 240, 240));
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        button.setPreferredSize(new Dimension(60, 60));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final JButton finalButton = button;
        final String finalLabel = label; // Declare label as final
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                finalButton.setBackground(new Color(220, 220, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                finalButton.setBackground(new Color(240, 240, 240));
            }
        });

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onButtonClick(finalLabel); // Use finalLabel here
            }
        });

        return button;
    }

    private void onButtonClick(String cmd) {
        switch (cmd) {
            case "C":
                clearAll();
                break;
            case "+":
            case "-":
            case "*":
            case "/":
            case "%":
                processOperator(cmd);
                break;
            case "=":
                computeResult();
                break;
            case ".":
                appendDecimal();
                break;
            default:
                appendDigit(cmd);
                break;
        }
    }

    private void appendDigit(String digit) {
        String text = displayField.getText();
        if (startNewNumber) {
            text = (digit.equals("0")) ? "0" : digit;
            startNewNumber = false;
        } else {
            if (text.equals("0")) {
                if (!digit.equals("0"))
                    text = digit;
                else
                    return;
            } else {
                text += digit;
            }
        }
        displayField.setText(text);
    }

    private void appendDecimal() {
        String text = displayField.getText();
        if (startNewNumber) {
            displayField.setText("0.");
            startNewNumber = false;
        } else if (!text.contains(".")) {
            displayField.setText(text + ".");
        }
    }

    private void clearAll() {
        displayField.setText("0");
        lastResult = 0;
        lastOperator = "";
        startNewNumber = true;
    }

    private void processOperator(String operator) {
        try {
            double currentValue = Double.parseDouble(displayField.getText());
            if (!lastOperator.isEmpty()) {
                lastResult = calculate(lastResult, currentValue, lastOperator);
                displayField.setText(decimalFormat.format(lastResult));
                updateHistory(lastResult, lastOperator, currentValue);
            } else {
                lastResult = currentValue;
            }
        } catch (NumberFormatException e) {
            displayField.setText("Error");
            startNewNumber = true;
            lastOperator = "";
            return;
        }
        lastOperator = operator;
        startNewNumber = true;
    }

    private void computeResult() {
        if (lastOperator.isEmpty()) {
            return; 
        }
        try {
            double currentValue = Double.parseDouble(displayField.getText());
            lastResult = calculate(lastResult, currentValue, lastOperator);
            displayField.setText(decimalFormat.format(lastResult));
            updateHistory(lastResult, lastOperator, currentValue);
        } catch (ArithmeticException ae) {
            displayField.setText("Err: Div by 0");
        } catch (NumberFormatException e) {
            displayField.setText("Error");
        } finally {
            lastOperator = "";
            startNewNumber = true;
        }
    }

    private void updateHistory(double result, String operator, double value) {
        String operation = String.format("%s %s %s = %s", lastResult, operator, value, decimalFormat.format(result));
        historyArea.append(operation + "\n");
    }

    private double calculate(double left, double right, String op) {
        switch (op) {
            case "+":
                return left + right;
            case "-":
                return left - right;
            case "*":
                return left * right;
            case "/":
                if (right == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return left / right;
            case "%":
                if (right == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return left % right;
            default:
                return right;
        }
    }

    private boolean handleKeyEvent(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED) {
            return false;
        }
        char keyChar = e.getKeyChar();
        int keyCode = e.getKeyCode();

        switch (keyChar) {
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                appendDigit(Character.toString(keyChar));
                return true;
            case '+':
                processOperator("+");
                return true;
            case '-':
                processOperator("-");
                return true;
            case '*':
                processOperator("*");
                return true;
            case '/':
                processOperator("/");
                return true;
            case '%':
                processOperator("%");
                return true;
            case '.':
                appendDecimal();
                return true;
            case '=':
            case '\n':
            case '\r':
                computeResult();
                return true;
            case '\b': // Backspace - clear current
                clearAll();
                return true;
            case 'c':
            case 'C':
                clearAll();
                return true;
        }

        return false;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setPreferredSize(new Dimension(0, 32));
        footer.setBackground(new Color(255, 255, 255, 180));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200, 120)));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(90, 90, 90));
        footer.add(statusLabel);

        return footer;
    }
}
