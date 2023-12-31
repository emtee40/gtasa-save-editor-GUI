package nl.paulinternet.gtasaveedit.view.selectable;

import nl.paulinternet.libsavegame.data.Zone;

import java.awt.*;

public class SelectableZone implements SelectableItemValue {

    private static final Color ZONE_BORDER = new Color(0xdd, 0xdd, 0xdd);

    private final Zone zone;
    private final Rectangle bounds;
    private boolean selected;

    public SelectableZone(Zone zone) {
        this.zone = zone;
        bounds = new Rectangle(zone.getX(), zone.getY(), zone.getWidth(), zone.getHeight());
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void paint(Graphics g) {
        if (selected) {
            g.setColor(Color.GRAY);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            g.setColor(zone.getColor());
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            g.setColor(ZONE_BORDER);
            g.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
        }
    }

    @Override
    public int getValue(int var) {
        return zone.getValue(var);
    }

    @Override
    public void setValue(int var, int value) {
        zone.setValue(var, value);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
