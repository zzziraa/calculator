import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ModernCalculator extends JFrame implements ActionListener {
    JTextField input;
    JLabel result;
    String expression = "";

    ModernCalculator() {
        setTitle("Calculator");
        setSize(350, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Color bgColor = new Color(30, 30, 30);
        Color fgColor = new Color(220, 220, 220);
        Color btnBgColor = new Color(50, 50, 50);
        Color btnFgColor = new Color(230, 230, 230);

        getContentPane().setBackground(bgColor);

        result = new JLabel("Result: ", SwingConstants.RIGHT);
        result.setFont(new Font("Arial", Font.BOLD, 20));
        result.setPreferredSize(new Dimension(350, 90));
        result.setForeground(fgColor);

        input = new JTextField();
        input.setFont(new Font("Arial", Font.PLAIN, 32));
        input.setPreferredSize(new Dimension(350, 90));
        input.setBackground(bgColor);
        input.setForeground(fgColor);
        input.setBorder(BorderFactory.createLineBorder(fgColor));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(bgColor);
        topPanel.add(result, BorderLayout.NORTH);
        topPanel.add(input, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        String[] buttons = {
            "C", "←", "", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "=", ""
        };

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 2, 2));
        buttonPanel.setBackground(bgColor);

        for (String b : buttons) {
            if (b.equals("")) {
                buttonPanel.add(new JLabel());
                continue;
            }
            JButton btn = new JButton(b);
            btn.setFont(new Font("SansSerif", Font.BOLD, 36));
            btn.setBackground(btnBgColor);
            btn.setForeground(btnFgColor);
            btn.setFocusPainted(false);
            btn.addActionListener(this);
            buttonPanel.add(btn);
        }

        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    double evaluate(String originalExpr) {
        final String expr = originalExpr.replaceAll(" ", "");

        try {
            return new Object() {
                int pos = -1, ch;

                void nextChar() {
                    ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    for (;;) {
                        if (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    for (;;) {
                        if (eat('*')) x *= parseFactor();
                        else if (eat('/')) x /= parseFactor();
                        else return x;
                    }
                }

                double parseFactor() {
                    if (eat('+')) return parseFactor();
                    if (eat('-')) return -parseFactor();

                    double x;
                    int startPos = this.pos;
                    if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(expr.substring(startPos, this.pos));
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }

                    return x;
                }
            }.parse();
        } catch (Exception e) {
            throw new RuntimeException("Invalid expression");
        }
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "C":
                expression = "";
                input.setText(expression);
                result.setText("Result: ");
                break;

            case "←":
                if (!expression.isEmpty()) {
                    expression = expression.substring(0, expression.length() - 1);
                    input.setText(expression);
                }
                break;

            case "=":
                try {
                    double res = evaluate(expression);
                    result.setText("Result: " + res);
                    input.setText(String.valueOf(res));
                    expression = String.valueOf(res);
                } catch (Exception ex) {
                    result.setText("Error");
                    input.setText("");
                    expression = "";
                }
                break;

            default:
                expression += cmd;
                input.setText(expression);
        }
    }

    public static void main(String[] args) {
        new ModernCalculator();
    }
}
