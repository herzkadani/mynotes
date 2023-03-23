package ch.bbzbl.mynotes.views.mynotes;

import ch.bbzbl.mynotes.data.entity.Folder;
import ch.bbzbl.mynotes.data.entity.Note;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.FolderService;
import ch.bbzbl.mynotes.data.service.NoteService;
import ch.bbzbl.mynotes.security.AuthenticatedUser;
import ch.bbzbl.mynotes.views.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

@PageTitle("MyNotes")
@Route(value = "MyNotes", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class MyNotesView extends Div {

	public static final String LEER = "Leer";
	public static final String MIN_WIDTH = "300px";
	public static final String BEARBEITEN = "Bearbeiten";
	private final VerticalLayout noteLayout = new VerticalLayout();
	VirtualList<Folder> folderList = new VirtualList<>();
	VirtualList<Note> noteList = new VirtualList<>();
	RichTextEditor textEditor = new RichTextEditor();
	SplitLayout splitLayout;
	HorizontalLayout notesSplit = new HorizontalLayout();
	private TextField popupField = new TextField();
	@Autowired
	private AuthenticatedUser authenticatedUser;
	private User user;
	private FolderService folderService = null;
	private NoteService noteService = null;
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
						noteService.update(currentNote);
						notes = folderService.get(currentFolder.getId()).get().getNotes();

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
					currentNote = note;
					notesSplit.remove(noteLayout);
					notesSplit.add(getNormalLayout());


					noteList.setItems(note.getFolder().getNotes());
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

		checkUser();

		if (this.folderService.list().isEmpty()) {
			emptyFolder();
		} else if (this.folderService.list().stream().findFirst().get().getNotes().isEmpty()) {
			currentFolder = folders.stream().findFirst().orElse(null);
			currentNote = new Note(LEER, LEER, currentFolder);
			noteService.update(currentNote);
			folders = folderService.list();
			notes = folders.stream().findFirst().get().getNotes();
		} else {
			folders = folderService.list();
			notes = folders.stream().findFirst().get().getNotes();
			currentFolder = folders.stream().findFirst().orElse(null);
			currentNote = notes.stream().findFirst().orElse(null);
		}

		folderList.setItems(folders);
		folderList.setRenderer(folderCardRenderer);
		folderList.setMinWidth(MIN_WIDTH);

		noteList.setItems(notes);
		noteList.setRenderer(noteCardRenderer);
		noteList.setMinWidth(MIN_WIDTH);

		Button addFolder = new Button("Neuer Ordner", e -> {
			newFolderOrNote(true);
		});

		VerticalLayout listLayout = new VerticalLayout(addFolder, folderList);

		Button addNote = new Button("Neue Notiz", e -> {

			newFolderOrNote(false);
		});
		VerticalLayout listLayoutN = new VerticalLayout(addNote, noteList);
		listLayoutN.setMaxWidth("350px");
		notesSplit.setPadding(true);
		notesSplit.add(listLayoutN);
		notesSplit.add(getNormalLayout());

		splitLayout = new SplitLayout(listLayout, notesSplit);
		splitLayout.setSplitterPosition(20);
		splitLayout.setHeightFull();
		splitLayout.addThemeVariants(SplitLayoutVariant.LUMO_MINIMAL);

		return splitLayout;
	}

	private void checkUser() {
		if (!Objects.isNull(authenticatedUser)) {
			if (authenticatedUser.get().isPresent()) {
				user = authenticatedUser.get().get();
			} else {
				authenticatedUser.logout();
				UI.getCurrent().navigate("login");
			}
		} else {
			
			UI.getCurrent().navigate("login");
		}
	}

	private void newFolderOrNote(boolean sourceFromFolderButton) {
		Dialog dialog;
		dialog = new Dialog();
		dialog.setHeaderTitle("Name");

		VerticalLayout dialogLayout = createDialogLayout();

		dialog.add(dialogLayout);

		Button saveButton = new Button("speichern", b -> {
			if (sourceFromFolderButton) {
				emptyFolder();
			} else {
				newNote();
			}

			folderList.setItems(folders);

			dialog.close();
		});
		saveButton.addClickShortcut(Key.ENTER);
		Button cancelButton = new Button("Cancel", be -> dialog.close());

		dialog.getFooter().add(saveButton);
		dialog.getFooter().add(cancelButton);

		add(dialog);

		dialog.open();
	}

	private void newNote() {
		currentNote = new Note(LEER, LEER, currentFolder);
		noteService.update(currentNote);
		folders = folderService.list();
		notes = folderService.get(currentFolder.getId()).get().getNotes();

		folderList.setItems(folders);
		noteList.setItems(notes);
	}

	private VerticalLayout createDialogLayout() {

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.add(popupField);
		verticalLayout.setPadding(false);
		verticalLayout.setSpacing(false);
		verticalLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
		verticalLayout.getStyle().set("width", "18rem").set("max-width", "100%");

		return verticalLayout;

	}

	private void emptyFolder() {
		currentFolder = new Folder(LEER, false, null, user);
		folderService.update(currentFolder);
		currentNote = new Note(LEER, LEER, currentFolder);
		noteService.update(currentNote);
		folders = folderService.list();
		notes = folders.stream().findFirst().get().getNotes();
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
