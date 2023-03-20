package ch.bbzbl.mynotes.views.mynotes;

import ch.bbzbl.mynotes.data.entity.Folder;
import ch.bbzbl.mynotes.data.entity.Note;
import ch.bbzbl.mynotes.data.service.FolderService;
import ch.bbzbl.mynotes.data.service.NoteService;
import ch.bbzbl.mynotes.views.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

import java.util.List;
import java.util.Objects;

@PageTitle("MyNotes")
@RouteAlias(value = "", layout = MainLayout.class)
@Route(value = "MyNotes", layout = MainLayout.class)
@PermitAll
public class MyNotesView extends Div {

	public static final String LEER = "Leer";
	public static final String MIN_WIDTH = "300px";
	public static final String BEARBEITEN = "Bearbeiten";
	private final FolderService folderService;
	private final NoteService noteService;
	private final VerticalLayout noteLayout = new VerticalLayout();
	VirtualList<Folder> folderList = new VirtualList<>();
	VirtualList<Note> noteList = new VirtualList<>();
	RichTextEditor textEditor = new RichTextEditor();
	SplitLayout splitLayout;
	HorizontalLayout notesSplit = new HorizontalLayout();
	private List<Folder> folders;
	private List<Note> notes;
	private Folder currentFolder;
	private Note currentNote;
	private long openFolderID;
	private long openNoteID;
	private final ComponentRenderer<Component, Folder> folderCardRenderer = new ComponentRenderer<>(
			folder -> {

				HorizontalLayout cardLayout = new HorizontalLayout();
				cardLayout.setMargin(true);

				H2 titel = new H2(folder.getTitel());

				titel.addClickListener(e -> {
					openFolderID = folder.getId();
					notes = folder.getNotes();
					if (notes.isEmpty()) {
						currentNote = new Note(LEER, LEER, currentFolder);
						notes.add(currentNote);
					} else {
						currentNote = notes.stream().findFirst().orElse(null);
					}
					noteList.setItems(notes);
					currentFolder = folder;

					folderList.setItems(folders);
				});
				if (!Objects.equals(folder.getId(), openNoteID) && cardLayout.getChildren().count() == 2) {
					cardLayout.removeAll();
					cardLayout.add(titel);
				} else if (Objects.equals(folder.getId(), openFolderID)) {
					Span open = new Span(createIcon(VaadinIcon.FOLDER_OPEN), new Span("open"));

					open.getElement().getThemeList().add("badge");

					cardLayout.add(titel);
					cardLayout.add(open);
				} else {
					cardLayout.add(titel);
				}
				return cardLayout;
			});
	private final ComponentRenderer<Component, Note> noteCardRenderer = new ComponentRenderer<>(
			note -> {

				HorizontalLayout cardLayout = new HorizontalLayout();

				cardLayout.setMargin(true);

				H3 titel = new H3(note.getTitel());

				titel.addClickListener(e -> {
					openNoteID = note.getId();
					notesSplit.remove(noteLayout);
					notesSplit.add(getNormalLayout());
					currentNote = note;

					noteList.setItems(notes);
				});


				if (!Objects.equals(note.getId(), openNoteID) && cardLayout.getChildren().count() == 2) {
					cardLayout.removeAll();
					cardLayout.add(titel);
				} else if (Objects.equals(note.getId(), openNoteID)) {
					Span open = new Span(createIcon(VaadinIcon.PENCIL), new Span("open"));

					open.getElement().getThemeList().add("badge");

					cardLayout.add(titel);
					cardLayout.add(open);
				} else {
					cardLayout.add(titel);
				}
				return cardLayout;
			});

	public MyNotesView(FolderService folderService, NoteService noteService) {
		this.folderService = folderService;
		this.noteService = noteService;
		setSizeFull();
		getStyle().set("text-align", "center");

		add(createContent());

	}

	private SplitLayout createContent() {

		folders = this.folderService.list();
		notes = folders.stream().findFirst().orElse(null).getNotes();

		folderList.setItems(folders);
		folderList.setRenderer(folderCardRenderer);
		folderList.setHeightFull();
		folderList.setMinWidth(MIN_WIDTH);

		noteList.setItems(notes);
		noteList.setRenderer(noteCardRenderer);
		noteList.setHeightFull();
		noteList.setMinWidth(MIN_WIDTH);

		if (folders == null) {
			currentFolder = new Folder(LEER, false, null, null);
			folderService.update(currentFolder);
		} else if (notes == null) {
			currentNote = new Note(LEER, LEER, currentFolder);
			noteService.update(currentNote);
		} else {
			currentFolder = folders.stream().findFirst().orElse(null);
			currentNote = notes.stream().findFirst().orElse(null);
		}

		notesSplit.setPadding(true);
		notesSplit.add(noteList);
		notesSplit.add(getNormalLayout());

		splitLayout = new SplitLayout(folderList, notesSplit);
		splitLayout.setSplitterPosition(20);
		splitLayout.setHeightFull();
		splitLayout.addThemeVariants(SplitLayoutVariant.LUMO_MINIMAL);

		return splitLayout;
	}

	private VerticalLayout getNormalLayout() {

		noteLayout.removeAll();

		H2 noteTitle = new H2(currentNote.getTitel());

		String valueAsHtml = currentNote.getContent();

		Label label = new Label(valueAsHtml);

		Button noteButton = new Button(BEARBEITEN, this::editMode);

		noteLayout.add(noteTitle, label, noteButton);

		return noteLayout;

	}

	private VerticalLayout getEditLayout() {

		noteLayout.removeAll();

		TextField noteTitle = new TextField("Titel");
		noteTitle.setValue(currentNote.getTitel());
		noteTitle.setClearButtonVisible(true);

		String valueAsHtml = currentNote.getContent();

		textEditor.asHtml().setValue(valueAsHtml);

		Button save = new Button("speichern", e -> {

			currentNote.setContent(textEditor.getValue());
			currentNote.setTitel(noteTitle.getValue());
			noteService.update(currentNote);

			notesSplit.remove(noteLayout);
			notesSplit.add(getNormalLayout());
		});
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button cancel = new Button("abbrechen", buttonClickEvent -> {
			notesSplit.remove(noteLayout);
			notesSplit.add(getNormalLayout());
		});
		cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

		HorizontalLayout buttonsLayout = new HorizontalLayout(save, cancel);

		noteLayout.add(noteTitle, textEditor, buttonsLayout);

		return noteLayout;
	}

	private void editMode(ClickEvent<Button> eB) {

		if (eB.getSource().getText().equals(BEARBEITEN)) {

			notesSplit.remove(noteLayout);
			notesSplit.add(getEditLayout());

		} else {

			notesSplit.remove(noteLayout);
			notesSplit.add(getNormalLayout());

		}
	}

	private Icon createIcon(VaadinIcon vaadinIcon) {
		Icon icon = vaadinIcon.create();
		icon.getStyle().set("padding", "var(--lumo-space-xs");
		return icon;
	}
}
