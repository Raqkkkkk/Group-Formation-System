package use_case.edit_project;

import Entities.Project;
import data_access.IProjectRepository;

public class EditProjectInteractor implements EditProjectInputBoundary {
    private final IProjectRepository projectRepository;
    private final EditProjectOutputBoundary projectPresenter;

    public EditProjectInteractor(IProjectRepository projectRepository, EditProjectOutputBoundary projectPresenter) {
        this.projectRepository = projectRepository;
        this.projectPresenter = projectPresenter;
    }

    /**
     * Edits a project with the provided input data.
     *
     * @param inputData the input data required to edit a project.
     */
    @Override
    public void editProject(EditProjectInputData inputData) {
        Project project = projectRepository.editProject(inputData.getTitle(), inputData.getBudget(), inputData.getDescription(), inputData.getTags());

        EditProjectOutputData outputData;
        if (project != null) {
            outputData = new EditProjectOutputData(project.getProjectId(), project.getProjectTitle(), project.getProjectBudget(), project.getProjectDescription(), project.getProjectTags(), true);
            projectPresenter.prepareSuccessView(outputData);
        } else {
            projectPresenter.prepareFailView("Failed to edit project.");
        }
    }
}
