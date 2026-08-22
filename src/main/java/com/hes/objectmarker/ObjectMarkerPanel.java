package com.hes.objectmarker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

public class ObjectMarkerPanel extends PluginPanel
{
	private final MarkerStore markerStore;
	private final JPanel markerList = new JPanel();

	public ObjectMarkerPanel(MarkerStore markerStore)
	{
		this.markerStore = markerStore;
		setLayout(new BorderLayout(0, 8));

		JButton addButton = new JButton("+ Add marker");
		addButton.addActionListener(e ->
		{
			markerStore.add(new MarkerDefinition(MarkerType.OBJECT, "", "", Color.CYAN, 40, true));
			rebuild();
		});

		markerList.setLayout(new BoxLayout(markerList, BoxLayout.Y_AXIS));
		markerList.setBackground(ColorScheme.DARK_GRAY_COLOR);

		add(addButton, BorderLayout.NORTH);
		add(markerList, BorderLayout.CENTER);
		rebuild();
	}

	public final void rebuild()
	{
		markerList.removeAll();
		for (MarkerDefinition marker : markerStore.getMarkers())
		{
			markerList.add(createMarkerCard(marker));
		}
		markerList.revalidate();
		markerList.repaint();
	}

	private JPanel createMarkerCard(MarkerDefinition marker)
	{
		JPanel card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		card.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));

		JComboBox<MarkerType> type = new JComboBox<>(MarkerType.values());
		type.setSelectedItem(marker.getType());
		type.addActionListener(e ->
		{
			marker.setType((MarkerType) type.getSelectedItem());
			markerStore.save();
		});

		JTextField match = new JTextField(marker.getMatch() == null ? "" : marker.getMatch());
		watch(match, value -> marker.setMatch(value));

		JTextField label = new JTextField(marker.getLabel() == null ? "" : marker.getLabel());
		watch(label, value -> marker.setLabel(value));

		JButton color = new JButton("Colour");
		color.setBackground(marker.getColor());
		color.addActionListener(e ->
		{
			Color chosen = JColorChooser.showDialog(this, "Marker colour", marker.getColor());
			if (chosen != null)
			{
				marker.setColor(chosen);
				color.setBackground(chosen);
				markerStore.save();
			}
		});

		JSlider opacity = new JSlider(0, 100, marker.getOpacity());
		opacity.setMajorTickSpacing(25);
		opacity.setPaintTicks(true);
		opacity.setPaintLabels(true);
		opacity.addChangeListener(e ->
		{
			marker.setOpacity(opacity.getValue());
			if (!opacity.getValueIsAdjusting())
			{
				markerStore.save();
			}
		});

		JSpinner padding = new JSpinner(new SpinnerNumberModel(marker.getPadding(), 0, 100, 1));
		padding.addChangeListener(e ->
		{
			marker.setPadding((Integer) padding.getValue());
			markerStore.save();
		});

		JCheckBox enabled = new JCheckBox("Enabled", marker.isEnabled());
		enabled.addActionListener(e ->
		{
			marker.setEnabled(enabled.isSelected());
			markerStore.save();
		});

		JButton delete = new JButton("Delete");
		delete.addActionListener(e ->
		{
			markerStore.remove(marker);
			SwingUtilities.invokeLater(this::rebuild);
		});

		card.add(row("Type", type));
		card.add(row("Match", match));
		card.add(row("Label", label));
		card.add(row("Colour", color));
		card.add(new JLabel("Opacity"));
		card.add(opacity);
		card.add(row("Padding (px)", padding));
		card.add(enabled);
		card.add(delete);
		return card;
	}

	private JPanel row(String name, java.awt.Component component)
	{
		JPanel row = new JPanel(new GridLayout(1, 2, 6, 0));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.add(new JLabel(name));
		row.add(component);
		return row;
	}

	private void watch(JTextField field, java.util.function.Consumer<String> setter)
	{
		field.getDocument().addDocumentListener(new DocumentListener()
		{
			private void update()
			{
				setter.accept(field.getText());
				markerStore.save();
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				update();
			}
		});
	}
}
