package io.github.alathra.boltux.gui;

import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.colorparser.paper.PaperComponentBuilder;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractGui {
    public static Component translate(String string) {
        return ColorParser.of(Translation.of(string))
            .papi()
            .mini()
            .legacy()
            .build();
    }

    public static Component translate(String string, Player p) {
        return ColorParser.of(Translation.of(string))
            .papi(p)
            .mini()
            .legacy()
            .build();
    }

    public static Component translate(String string, Consumer<PaperComponentBuilder> consumer) {
        final var c = ColorParser.of(Translation.of(string))
            .papi()
            .mini()
            .legacy();
        consumer.accept(c);
        return c.build();
    }

    public static Component translate(String string, Player p, Consumer<PaperComponentBuilder> consumer) {
        final var c = ColorParser.of(Translation.of(string))
            .papi(p)
            .mini(p)
            .legacy();
        consumer.accept(c);
        return c.build();
    }

    public static List<Component> translateList(String string) {
        return Translation.ofList(string).stream()
            .map(s -> ColorParser.of(s)
                .papi()
                .mini()
                .legacy()
                .build()
            )
            .toList();
    }

    public static List<Component> translateList(String string, Player p) {
        return Translation.ofList(string).stream()
            .map(s -> ColorParser.of(s)
                .papi(p)
                .mini(p)
                .legacy()
                .build()
            )
            .toList();
    }

    public static List<Component> translateList(String string, Consumer<PaperComponentBuilder> consumer) {
        return Translation.ofList(string).stream()
            .map(s -> {
                    final var c = ColorParser.of(s)
                        .papi()
                        .mini()
                        .legacy();

                    consumer.accept(c);
                    return c.build();
                }
            )
            .toList();
    }

    public static List<Component> translateList(String string, Player p, Consumer<PaperComponentBuilder> consumer) {
        return Translation.ofList(string).stream()
            .map(s -> {
                    final var c = ColorParser.of(s)
                        .papi(p)
                        .mini(p)
                        .legacy();

                    consumer.accept(c);
                    return c.build();
                }
            )
            .toList();
    }

    public static ItemStack borderItem() {
        final ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        item.editMeta(meta -> {
            meta.customName(Component.empty());
            meta.lore(List.of());
            meta.setHideTooltip(true);
        });
        return item;
    }

    public static ItemStack closeButton() {
        final ItemStack button = new ItemStack(Material.BARRIER);
        button.editMeta(meta -> {
            meta.customName(translate("gui.buttons.close.name"));
            meta.lore(translateList("gui.buttons.close.lore"));
        });
        return button;
    }

    public static ItemStack backButton() {
        final ItemStack button = new ItemStack(Material.PAPER);
        button.editMeta(meta -> {
            meta.customName(translate("gui.buttons.back.name"));
            meta.lore(translateList("gui.buttons.back.lore"));
        });
        return button;
    }

    public static ItemStack nextButton() {
        final ItemStack button = new ItemStack(Material.ARROW);
        button.editMeta(meta -> {
            meta.customName(translate("gui.buttons.next.name"));
            meta.lore(translateList("gui.buttons.next.lore"));
        });
        return button;
    }

    public static ItemStack previousButton() {
        final ItemStack button = new ItemStack(Material.ARROW);
        button.editMeta(meta -> {
            meta.customName(translate("gui.buttons.previous.name"));
            meta.lore(translateList("gui.buttons.previous.lore"));
        });
        return button;
    }
}
