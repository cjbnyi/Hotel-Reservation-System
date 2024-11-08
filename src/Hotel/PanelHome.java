package Hotel;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class PanelHome extends JPanel {
    private JButton createButton;
    private JButton viewButton;
    private JButton bookButton;
    private JButton manageButton;
    private JButton quitButton;

    PanelHome(ComponentFactory componentFactory){
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setBackground(componentFactory.getRandomColor());

        String[] names = {
            "Create Hotel",
            "View Hotel",
            "Manage Hotel",
            "Book Hotel",
            "Quit Manager"
        };

        ArrayList<JButton> homeButtons = componentFactory.createMultipleJButtons(names, 200, 50);
        CompBuilderBoxLayout cbBoxCenter = new CompBuilderBoxLayout(CENTER_ALIGNMENT);
        cbBoxCenter.setParent(this);
        cbBoxCenter.setSpacing(25);
        cbBoxCenter.setAutoSpace(true);
        
        cbBoxCenter.addSpacing();
        cbBoxCenter.setChild(componentFactory.createJLabelHeading("Menu:"));

        int i = 0;
        for (JButton button : homeButtons){

            switch(i) {
                case 0:
                    this.createButton = button;
                    break;
                case 1:
                    this.viewButton = button;
                    break;
                case 2:
                    this.manageButton = button;
                    break;
                case 3:
                    this.bookButton = button;
                    break;
                case 4:
                    this.quitButton = button;
                    break;
            }
            i++;
            cbBoxCenter.setChild(button);
        }

    }

    public void setActionListener(ActionListener listener){
        createButton.addActionListener(listener);
        viewButton.addActionListener(listener);
        manageButton.addActionListener(listener);
        bookButton.addActionListener(listener);
        quitButton.addActionListener(listener);
    }

}
