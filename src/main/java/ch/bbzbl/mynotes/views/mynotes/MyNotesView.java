package ch.bbzbl.mynotes.views.mynotes;

import ch.bbzbl.mynotes.data.entity.Folder;
import ch.bbzbl.mynotes.data.entity.Note;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import ch.bbzbl.mynotes.views.MainLayout;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@PageTitle("MyNotes")
@RouteAlias(value = "", layout = MainLayout.class)
@Route(value = "MyNotes", layout = MainLayout.class)
@PermitAll
public class MyNotesView extends Div {

    private List<Folder> folders;
    private List<Note> notes;

    private ComponentRenderer<Component, Note> noteCardRenderer = new ComponentRenderer<Component, Note>(
            note -> {

                HorizontalLayout cardLayout = new HorizontalLayout();
                cardLayout.setMargin(true);

                H2 titel = new H2(note.getTitel());

                cardLayout.add(titel);

                return cardLayout;

    });

    private ComponentRenderer<Component, Folder> folderCardRenderer = new ComponentRenderer<Component, Folder>(
            folder -> {

                HorizontalLayout cardLayout = new HorizontalLayout();
                cardLayout.setMargin(true);

                H2 titel = new H2(folder.getTitel());

                cardLayout.add(titel);

                return cardLayout;

            });

    public MyNotesView(){

        setSizeFull();
        getStyle().set("text-align", "center");

        add(createContent());

    }

    private SplitLayout createContent() {

        VirtualList<Folder> folderList = new VirtualList<>();

        folderList.setItems(folders);
        folderList.setRenderer(folderCardRenderer);

        VirtualList<Note> noteList = new VirtualList<>();

        noteList.setItems(notes);
        noteList.setRenderer(noteCardRenderer);


        SplitLayout splitLayout = new SplitLayout(folderList, noteList);


        return splitLayout;
    }

}
