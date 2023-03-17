package ch.bbzbl.mynotes.views.mynotes;

import ch.bbzbl.mynotes.data.entity.Folder;
import ch.bbzbl.mynotes.data.entity.Note;
import ch.bbzbl.mynotes.data.service.FolderService;
import ch.bbzbl.mynotes.data.service.NoteService;
import ch.bbzbl.mynotes.views.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@PageTitle("MyNotes")
@RouteAlias(value = "", layout = MainLayout.class)
@Route(value = "MyNotes", layout = MainLayout.class)
@PermitAll
public class MyNotesView extends Div {

	private final String BEARBEITEN = "Bearbeiten";
	private final String NORMAL = "Normal";
	VirtualList<Folder> folderList = new VirtualList<>();
	VirtualList<Note> noteList = new VirtualList<>();
	RichTextEditor textEditor = new RichTextEditor();
	SplitLayout splitLayout;
	Button noteButton;
	private VerticalLayout noteLayout = new VerticalLayout();
	//folderService
	private List<Folder> folders;
	private List<Note> notes;
	private FolderService folderService;
	private NoteService noteService;
	private Folder currentFolder;
	private Note currentNote;
	private ComponentRenderer<Component, Note> noteCardRenderer = new ComponentRenderer<Component, Note>(
			note -> {

				HorizontalLayout cardLayout = new HorizontalLayout();

				cardLayout.setMargin(true);

				H2 titel = new H2(note.getTitel());

				titel.addClickListener(e -> {

					splitLayout.remove(noteLayout);
					splitLayout.addToSecondary(getNormalLayout());
					currentNote = note;
				});

				cardLayout.add(titel);

				return cardLayout;

			});

	private ComponentRenderer<Component, Folder> folderCardRenderer = new ComponentRenderer<Component, Folder>(
			folder -> {

				HorizontalLayout cardLayout = new HorizontalLayout();
				cardLayout.setMargin(true);

				H2 titel = new H2(folder.getTitel());

				titel.addClickListener(e -> {
					notes = folder.getNotes();
					currentFolder = folder;
				});

				cardLayout.add(titel);

				return cardLayout;

			});

	public MyNotesView(FolderService folderService, NoteService noteService) {
		this.folderService = folderService;
		this.noteService = noteService;
		folders = folderService.list();
		notes = folders.stream().findFirst().orElse(null).getNotes();
		setSizeFull();
		getStyle().set("text-align", "center");

		add(createContent());

	}

	private SplitLayout createContent() {

		folderList.setItems(folders);
		folderList.setRenderer(folderCardRenderer);

		noteList.setItems(notes);
		noteList.setRenderer(noteCardRenderer);

		if (folders == null) {
			currentFolder = new Folder();
		} else if (notes == null) {
			currentFolder = folders.stream().findFirst().orElse(null);
			currentNote = new Note("Leer", "Leer", currentFolder);
		} else {
			currentNote = notes.stream().findFirst().orElse(null);
		}
		HorizontalLayout notesSplit = new HorizontalLayout();

		notesSplit.add(noteList);
		notesSplit.add(getNormalLayout());

		splitLayout = new SplitLayout(folderList, notesSplit);

		return splitLayout;
	}

	private HorizontalLayout getLeftSplit() {
		HorizontalLayout notesSplit = new HorizontalLayout();

		notesSplit.add(noteList);
		notesSplit.add(getNormalLayout());
		return notesSplit;
	}

	private VerticalLayout getNormalLayout() {

		noteLayout.removeAll();

		H2 noteTitel = new H2(currentNote.getTitel());

		String valueAsHtml = currentNote.getContent();

		Label label = new Label(valueAsHtml);

		noteButton = new Button(BEARBEITEN, this::editMode);

		noteLayout.add(noteTitel, label, noteButton);

		return noteLayout;

	}

	private VerticalLayout getEditLayout() {

		noteLayout.removeAll();

		H2 noteTitel = new H2(currentNote.getTitel());

		String valueAsHtml = currentNote.getContent();

		textEditor.asHtml().setValue(valueAsHtml);
		textEditor.addValueChangeListener(e -> {

			currentNote.setContent(textEditor.getValue());
			noteService.update(currentNote);

		});

		noteButton = new Button(BEARBEITEN, this::editMode);

		noteLayout.add(noteTitel, textEditor, noteButton);

		return noteLayout;
	}

	private void editMode(ClickEvent<Button> eB) {

		if (eB.getSource().getText().equals(BEARBEITEN)) {

			noteButton.setText(NORMAL);
			splitLayout.remove(noteLayout);
			add(getNormalLayout());

		} else {

			noteButton.setText(BEARBEITEN);
			splitLayout.remove(noteLayout);
			add(getEditLayout());

		}
	}

}
