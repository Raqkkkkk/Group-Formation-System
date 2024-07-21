package view;

import usecase.getapplications.GetApplicationsController;
import usecase.getloggedinuser.GetLoggedInUserController;
import usecase.getprojects.GetProjectsController;
import view.components.ButtonAction;
import view.components.ButtonColumn;
import viewmodel.EditProjectPanelViewModel;
import viewmodel.MyProjectsPanelViewModel;
import viewmodel.ViewManagerModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A panel for displaying and managing the user's projects.
 */
public class MyProjectsPanel extends JPanel implements ActionListener, PropertyChangeListener {

    private final GetProjectsController getProjectsController;
    private final MyProjectsPanelViewModel myProjectsPanelViewModel;
    private final ViewManagerModel viewManagerModel;
    private final EditProjectPanelViewModel editProjectPanelViewModel;
    private final EditProjectPanel editProjectPanel;
    private final GetLoggedInUserController getLoggedInUserController;
    private final JTable infoTable = new JTable();
    private final int[] columnWidths = {200, 400, 100};
    private final String[] columnNames = {"Project Title", "Description", "Edit"};
    private final JScrollPane infoPanel = new JScrollPane(infoTable);

    /**
     * Constructs a MyProjectsPanel.
     *
     * @param myProjectsPanelViewModel the view model for the user's projects
     * @param viewManagerModel the view manager model
     * @param getLoggedInUserController the controller for getting the logged-in user
     * @param getProjectsController the controller for getting projects
     * @param getApplicationsController the controller for getting applications
     * @param editProjectPanelViewModel the view model for editing a project
     * @param editProjectPanel the panel for editing a project
     */
    public MyProjectsPanel(MyProjectsPanelViewModel myProjectsPanelViewModel,
                           ViewManagerModel viewManagerModel,
                           GetLoggedInUserController getLoggedInUserController,
                           GetProjectsController getProjectsController,
                           GetApplicationsController getApplicationsController,
                           EditProjectPanelViewModel editProjectPanelViewModel,
                           EditProjectPanel editProjectPanel) {
        this.viewManagerModel = viewManagerModel;
        this.getLoggedInUserController = getLoggedInUserController;
        this.myProjectsPanelViewModel = myProjectsPanelViewModel;
        this.getProjectsController = getProjectsController;
        this.editProjectPanelViewModel = editProjectPanelViewModel;
        this.editProjectPanel = editProjectPanel;

        myProjectsPanelViewModel.addPropertyChangeListener(this);
        viewManagerModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(infoPanel);

        JButton refreshButton = new JButton("Refresh");
        this.add(refreshButton);

        refreshButton.addActionListener(e -> {
            getProjectsController.getProjects();
        });
    }

    /**
     * Adds projects to the table.
     *
     * @param projectData the data of the projects
     */
    private void addProjects(Object[][] projectData){
        ArrayList<ButtonAction> editButtonActions = new ArrayList<>();
        ArrayList<ButtonAction> applicationButtonActions = new ArrayList<>();
        ArrayList<ButtonAction> deleteButtonActions = new ArrayList<>();

        Object[][] info = new Object[projectData.length][3];
        for (int i = 0; i < projectData.length; i++) {
            info[i][0] = projectData[i][1];
            info[i][1] = projectData[i][2];
            info[i][2] = "Edit";
            int finalI = i;
            editButtonActions.add(new ButtonAction() {
                @Override
                public void onClick() {
                    int projectId = (int) projectData[finalI][0];
                    String projectTitle = (String) projectData[finalI][1];
                    String projectDescription = (String) projectData[finalI][2];
                    double projectBudget = (double) projectData[finalI][3];
                    HashSet<String> projectTags = (HashSet<String>) projectData[finalI][4];
                    int editorId = myProjectsPanelViewModel.getLoggedInUser().getUserId();

                    editProjectPanelViewModel.setProjectDetails(projectId, projectTitle, projectBudget,
                            projectDescription, projectTags, editorId);
                    editProjectPanelViewModel.initDetails();

                    // Display editProjectPanel in your application window
                    JFrame editFrame = new JFrame("Edit Project");
                    editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    editFrame.setSize(400, 300);
                    editFrame.add(editProjectPanel);

                    editFrame.setVisible(true);
                }
            });
        }

        DefaultTableModel infoTableModel = new DefaultTableModel(info, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make only the button column editable
                return column >= 2;
            }
        };
        infoTable.setModel(infoTableModel);

        ButtonColumn editColumn = new ButtonColumn(infoTable, 2);
        editColumn.setActions(editButtonActions);

        TableColumnModel columnModel = infoTable.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++) {
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // No implementation needed
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("dataUpdate")){
            Object[][] data = (Object[][]) evt.getNewValue();
            addProjects(data);
        }
        if (evt.getPropertyName().equals("login")) {
            getLoggedInUserController.getLoggedInUser();
            boolean login = (boolean) evt.getNewValue();
            if (login) {
                getProjectsController.getProjects();
            }
        }
        if (evt.getPropertyName().equals("error")) {
            String errorMessage = (String) evt.getNewValue();
            JOptionPane.showMessageDialog(this, errorMessage);
        }
        if (evt.getPropertyName().equals("deleteProject")) {
            JOptionPane.showMessageDialog(null, "Successfully deleted project");
        }
    }
}
