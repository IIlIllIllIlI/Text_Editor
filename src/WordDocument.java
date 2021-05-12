import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WordDocument extends JScrollPane implements DocumentListener{

    private String name;
    private String path;
    private JTextArea text;
    private boolean newDocument;
    private boolean unsaved;

    public WordDocument(boolean newDocument) {
        this("untitled", "", new JTextArea());
        this.newDocument = newDocument;
    }

    public WordDocument(String name, String path, JTextArea text) {
        super(text);
        this.name = name;
        this.path = path;
        this.text = text;
        // 监听文件是否有改动
        this.text.getDocument().addDocumentListener(this);
    }

    public void save() {
       saveAs(path);
    }

    // 保存成功返回 true
    // 否则返回 false
    public boolean saveAs(String path) {
        try {
            File file = new File(path);

            if (file.exists()) {
                // 如果SaveAs 覆盖的是自己本身不弹出提示框
                if (!this.path.equals(file.getAbsolutePath())) {
                    int value = JOptionPane.showConfirmDialog(null,
                            "要覆盖吗？", "警告", JOptionPane.YES_NO_OPTION);
                    if (value == JOptionPane.NO_OPTION) {
                        return false;
                    }
                }

            }
            this.name = file.getName();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            bw.write(text.getText());
            bw.close();

            // set the path to this new File
            this.path = file.getAbsolutePath();
            this.newDocument = false;
            this.unsaved = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        update();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        update();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        update();
    }

    // 文件有改动，更新状态
    private void update() {
        unsaved = true;
        name = name + "*";
    }

    @Override
    public String getName() {
        return name;
    }

    public JTextArea getText() {
        return text;
    }

    public boolean isNewDocument() {
        return newDocument;
    }

    public boolean isUnsaved() {
        return unsaved;
    }


}
