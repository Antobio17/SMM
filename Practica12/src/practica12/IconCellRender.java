/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica12;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public class IconCellRender implements ListCellRenderer<ImageIcon>{

    @Override
    public Component getListCellRendererComponent(JList<? extends ImageIcon> list,
            ImageIcon value, int index, boolean isSelected, boolean cellHasFocus) {
        IconPanel panel = new IconPanel(value);
        return panel;
    }
    
}
