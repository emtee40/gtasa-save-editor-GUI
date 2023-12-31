package nl.paulinternet.gtasaveedit.view.pages.schools;

import nl.paulinternet.gtasaveedit.view.connected.ConnectedTextField;
import nl.paulinternet.gtasaveedit.view.pages.Page;
import nl.paulinternet.gtasaveedit.view.swing.Table;
import nl.paulinternet.gtasaveedit.view.swing.YBox;
import nl.paulinternet.libsavegame.variables.Variables;

import javax.swing.*;

import static nl.paulinternet.gtasaveedit.view.pages.schools.SchoolConsts.*;

public class PageSchools extends Page {


    public PageSchools() {
        super("Schools");

        Table table = new Table();
        table.setSpacing(10, 3);

        // Driving
        table.add(new JLabel("Driving"), 0, 2);
        for (int i = 0; i < 12; i++) {
            table.add(new JLabel(driving[i]), 1, 2 + i);
            table.add(new ConnectedTextField(Variables.get().schoolDriving.get(i)), 2, 2 + i);
            table.add(new JLabel("%"), 3, 2 + i);
        }

        table.add(new JSeparator(), 0, 14, 4, 1);

        // Flying
        table.add(new JLabel("Flying"), 0, 15);
        for (int i = 0; i < 10; i++) {
            table.add(new JLabel(flying[i]), 1, 15 + i);
            table.add(new ConnectedTextField(Variables.get().schoolFlying.get(i)), 2, 15 + i);
            table.add(new JLabel("%"), 3, 15 + i);
        }

        table.add(new JSeparator(), 0, 25, 4, 1);

        // Boat
        table.add(new JLabel("Boat"), 0, 26);
        for (int i = 0; i < 5; i++) {
            table.add(new JLabel(boat[i]), 1, 26 + i);
            table.add(new ConnectedTextField(Variables.get().schoolBoat.get(i)), 2, 26 + i);
            table.add(new JLabel(boatUnit[i]), 3, 26 + i);
        }

        table.add(new JSeparator(), 0, 31, 4, 1);

        // Bike
        table.add(new JLabel("Bike"), 0, 32);
        for (int i = 0; i < 6; i++) {
            table.add(new JLabel(bike[i]), 1, 32 + i);
            table.add(new ConnectedTextField(Variables.get().schoolBike.get(i)), 2, 32 + i);
            table.add(new JLabel("%"), 3, 32 + i);
        }

        YBox ybox = new YBox();
        ybox.add(new JLabel("<html>You can here edit all the school values.<br />For the percent values, use 70% for bronze, 85% for silver and 100% for gold.<br />Before you can edit the driving school scores, you have to visit it once.<br />You cannot edit the last exercises of the driving, flying and boat schools if you didn't complete the school.</html>"));
        ybox.addSpace(10);
        ybox.add(table, 0, 0.0f, 0.0f);
        ybox.setBorder(10);
        setComponent(ybox, true);
    }
}
