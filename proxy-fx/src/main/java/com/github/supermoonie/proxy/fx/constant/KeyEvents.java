package com.github.supermoonie.proxy.fx.constant;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * @author supermoonie
 * @date 2020-11-19
 */
public interface KeyEvents {

    KeyCodeCombination MAC_KEY_CODE_COMBINATION = new KeyCodeCombination(KeyCode.C, KeyCombination.META_DOWN);
    KeyCodeCombination WIN_KEY_CODE_COPY = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
}
