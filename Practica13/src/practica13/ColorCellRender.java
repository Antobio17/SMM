/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica13;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Antonio Jiménez Rodríguez
 */
public class ColorCellRender implements ListCellRenderer<Color>{

    @Override
    public Component getListCellRendererComponent(JList<? extends Color> list,
            Color value, int index, boolean isSelected, boolean cellHasFocus) {
        ColorPanel panel = new ColorPanel(value);
        return panel;
    }
    
}
