import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Words extends JFrame implements ActionListener{

    private JTabbedPane tabPane;

    public static void main(String[] args) {
        new Words().setVisible(true);
    }

    private Words() {
        super("Text Editor");
        setSize(800, 600);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                safeDelete();
            }
        });
        initialize();
    }



    private void initialize() {
        tabPane = new JTabbedPane();
        // default document
        WordDocument doc = new WordDocument(true);
        tabPane.addTab(doc.getName(), doc);

        // 菜单条
        JMenuBar bar = new JMenuBar();

        //菜单栏
        JMenu file = new JMenu("File");
        // 菜单选项
        JMenuItem newDoc = new JMenuItem("New");
        JMenuItem open = new JMenuItem("Open");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem saveAs = new JMenuItem("Save as");
        JMenuItem exit = new JMenuItem("Exit");

        // 添加快捷键
        newDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() |
                InputEvent.SHIFT_DOWN_MASK));

        // 添加 actionListener
        JMenuItem[] items = {newDoc, open, save, saveAs, exit};
        for (JMenuItem item : items) {
            item.addActionListener(this);
        }

        file.add(newDoc);
        file.add(open);
        file.add(save);
        file.add(saveAs);
        file.addSeparator();   // 加分隔线
        file.add(exit);

        bar.add(file);

        add(tabPane);
        setJMenuBar(bar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("New")) {
            newDoc();
        } else if (e.getActionCommand().equals("Open")) {
             open();
        } else if (e.getActionCommand().equals("Save")) {
            save();
        } else if (e.getActionCommand().equals("Save as")) {
            saveAs();
        } else if (e.getActionCommand().equals("Exit")) {
            safeDelete();
        }

    }

    private void newDoc() {
        WordDocument doc = new WordDocument(true);
        tabPane.addTab(doc.getName(), doc);
        // 自动选中新建页面
        tabPane.setSelectedComponent(doc);
    }

    private void open() {
        JFileChooser chooser = new JFileChooser("./");
        int returned = chooser.showOpenDialog(this);
        if (returned == JFileChooser.APPROVE_OPTION) {   // 成功选择文件
            File file = chooser.getSelectedFile();
            WordDocument doc = new WordDocument(file.getName(), file.getAbsolutePath(), new JTextArea());
            tabPane.addTab(file.getName(), doc);
            tabPane.setSelectedComponent(doc);

            // 读取文件里的内容，输入到JTextArea里
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null)  {
                    doc.getText().append(line + "\n");
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void save() {
        WordDocument doc = (WordDocument) tabPane.getSelectedComponent();
        if (doc.isNewDocument()) {
            saveAs();
        } else {
            doc.save();
        }
    }

    private void saveAs() {
        JFileChooser chooser = new JFileChooser("./");

        int returned = chooser.showOpenDialog(this);

        if(returned == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            WordDocument doc = (WordDocument) tabPane.getSelectedComponent();
            if (doc.saveAs(file.getAbsolutePath())) {  // true 如果保存成功， 改变tab里的文件名
                tabPane.setTitleAt(tabPane.getSelectedIndex(), file.getName());
            }
        }
    }

    // 如果有未保存文件则弹出提示框
    private void safeDelete() {
        for (int i = 0; i < tabPane.getTabCount(); i++) {
            WordDocument doc = (WordDocument) tabPane.getComponentAt(i);

            if (doc.isUnsaved()) {
                int value = JOptionPane.showConfirmDialog(null,
                        "还未保存要直接关闭吗", "警告", JOptionPane.YES_NO_OPTION);
                if (value == JOptionPane.NO_OPTION) {
                    return;
                } else {
                   System.exit(0);
                }
            }
        }
        System.exit(0);

    }



}
