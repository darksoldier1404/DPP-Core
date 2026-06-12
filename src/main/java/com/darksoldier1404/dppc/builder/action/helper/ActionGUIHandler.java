package com.darksoldier1404.dppc.builder.action.helper;

import com.darksoldier1404.dppc.DPPCore;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActionGUIHandler implements Listener {
    private final Map<UUID, Tuple<ActionGUI, ActionType>> pendingInput = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof DInventory)) return;
        DInventory inv = (DInventory) e.getInventory().getHolder();
        if (!(inv.getObj() instanceof ActionGUI)) return;
        ActionGUI ag = (ActionGUI) inv.getObj();
        if (!inv.isValidHandler(ag.getPlugin())) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;
        if (e.getInventory().getType() == InventoryType.PLAYER) return;

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        // --- Navigation / control buttons ---
        if (NBT.hasTagKey(item, "dppc.action")) {
            String action = NBT.getStringTag(item, "dppc.action");
            switch (action) {
                case "add":
                    ag.openActionSelectGUI(p);
                    return;
                case "save":
                    YamlConfiguration raw = ag.getActionBuilder().exportToYaml();
                    ConfigUtils.saveCustomData(ag.getPlugin(), raw, ag.getActionBuilder().getActionName(), "actions");
                    DPPCore.actions.put(ag.getActionBuilder().getActionName(), ag.getActionBuilder());
                    p.closeInventory();
                    p.sendMessage("§a액션 §f'" + ag.getActionBuilder().getActionName() + "' §a저장 완료!");
                    return;
                case "prev":
                case "next":
                    int targetPage = NBT.getIntegerTag(item, "dppc.page");
                    ag.openActionBuilderGUI(p, targetPage);
                    return;
            }
        }

        // --- Action list (channel 0) ---
        if (inv.isValidChannel(0) && NBT.hasTagKey(item, "dppc.actionType")) {
            int globalIndex = NBT.getIntegerTag(item, "dppc.actionIndex");

            if (e.getClick() == ClickType.RIGHT) {
                if (globalIndex < ag.getActionBuilder().getActions().size()) {
                    ag.getActionBuilder().getActions().remove(globalIndex);
                }
                ag.openActionBuilderGUI(p, ag.currentPage);
                return;
            }

            if (e.getClick() == ClickType.LEFT) {
                ActionType actionType = ActionType.valueOf(NBT.getStringTag(item, "dppc.actionType"));
                ag.getActionBuilder().setCurrentEditIndex(globalIndex);
                ag.getActionBuilder().setEditing(true);
                pendingInput.put(p.getUniqueId(), Tuple.of(ag, actionType));
                sendInputPrompt(p, actionType);
                p.closeInventory();
            }
            return;
        }

        // --- Action type selector (channel 1) ---
        if (inv.isValidChannel(1) && NBT.hasTagKey(item, "dppc.actionTypeSelect")) {
            ActionType actionType = ActionType.valueOf(NBT.getStringTag(item, "dppc.actionTypeSelect"));

            // No-parameter actions: add immediately
            if (isNoParamAction(actionType)) {
                addNoParamAction(ag, actionType);
                ag.openActionBuilderGUI(p, ag.currentPage);
                return;
            }

            pendingInput.put(p.getUniqueId(), Tuple.of(ag, actionType));
            sendInputPrompt(p, actionType);
            p.closeInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        UUID uid = e.getPlayer().getUniqueId();
        if (!pendingInput.containsKey(uid)) return;

        e.setCancelled(true);
        ActionGUI ag = pendingInput.get(uid).getA();
        ActionType type = pendingInput.get(uid).getB();
        Player p = e.getPlayer();
        String input = e.getMessage().trim();

        if (input.equalsIgnoreCase("cancel")) {
            pendingInput.remove(uid);
            ag.getActionBuilder().setEditing(false);
            Bukkit.getScheduler().runTaskLater(ag.getPlugin(), () -> ag.openActionBuilderGUI(p, ag.currentPage), 1L);
            p.sendMessage("§c취소되었습니다.");
            return;
        }

        boolean success = applyInput(ag, type, input, p);
        if (success) {
            pendingInput.remove(uid);
            Bukkit.getScheduler().runTaskLater(ag.getPlugin(), () -> ag.openActionBuilderGUI(p, ag.currentPage), 1L);
        }
    }

    private boolean applyInput(ActionGUI ag, ActionType type, String input, Player p) {
        try {
            switch (type) {
                case DELAY:
                    ag.getActionBuilder().delay(Long.parseLong(input));
                    break;
                case SEND_MESSAGE:
                    ag.getActionBuilder().sendMessage(input);
                    break;
                case SEND_TITLE:
                    // Format: title|subtitle or title|subtitle|fadeIn|stay|fadeOut
                    String[] titleParts = input.split("\\|", -1);
                    String title = titleParts.length > 0 ? titleParts[0] : "";
                    String subtitle = titleParts.length > 1 ? titleParts[1] : "";
                    int fadeIn = titleParts.length > 2 ? Integer.parseInt(titleParts[2]) : 10;
                    int stay = titleParts.length > 3 ? Integer.parseInt(titleParts[3]) : 70;
                    int fadeOut = titleParts.length > 4 ? Integer.parseInt(titleParts[4]) : 20;
                    ag.getActionBuilder().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
                    break;
                case SEND_ACTIONBAR:
                    ag.getActionBuilder().sendActionBar(input);
                    break;
                case BROADCAST:
                    ag.getActionBuilder().broadcast(input);
                    break;
                case BROADCAST_WORLD:
                    ag.getActionBuilder().broadcastWorld(input);
                    break;
                case EXECUTE_AS_ADMIN:
                    ag.getActionBuilder().executeCommandAsAdmin(input);
                    break;
                case EXECUTE_AS_PLAYER:
                    ag.getActionBuilder().executeCommandAsPlayer(input);
                    break;
                case TELEPORT: {
                    // Format: world x,y,z
                    String[] tpParts = input.split("\\s+");
                    if (tpParts.length != 2) {
                        p.sendMessage("§c형식: <world> <x,y,z>");
                        return false;
                    }
                    String[] coords = tpParts[1].split(",");
                    if (coords.length != 3) {
                        p.sendMessage("§c좌표 형식: x,y,z");
                        return false;
                    }
                    ag.getActionBuilder().teleport(tpParts[0],
                            Double.parseDouble(coords[0]),
                            Double.parseDouble(coords[1]),
                            Double.parseDouble(coords[2]));
                    break;
                }
                case SET_GAMEMODE:
                    ag.getActionBuilder().setGamemode(input.toUpperCase());
                    break;
                case GIVE_EXP:
                    ag.getActionBuilder().giveExp(Integer.parseInt(input));
                    break;
                case TAKE_EXP:
                    ag.getActionBuilder().takeExp(Integer.parseInt(input));
                    break;
                case SET_HEALTH:
                    ag.getActionBuilder().setHealth(Double.parseDouble(input));
                    break;
                case SET_HUNGER:
                    ag.getActionBuilder().setHunger(Integer.parseInt(input));
                    break;
                case KICK:
                    ag.getActionBuilder().kick(input);
                    break;
                case PLAY_SOUND: {
                    String[] sp = input.split("\\s+");
                    String sound = sp[0];
                    float volume = sp.length > 1 ? Float.parseFloat(sp[1]) : 1.0f;
                    float pitch = sp.length > 2 ? Float.parseFloat(sp[2]) : 1.0f;
                    ag.getActionBuilder().playSound(sound, volume, pitch);
                    break;
                }
                case PLAY_PARTICLE: {
                    String[] pp = input.split("\\s+");
                    if (pp.length < 2) {
                        p.sendMessage("§c형식: <파티클명> <개수> [ox] [oy] [oz]");
                        return false;
                    }
                    ag.getActionBuilder().playParticle(pp[0], Integer.parseInt(pp[1]));
                    break;
                }
                case ADD_POTION_EFFECT: {
                    String[] ep = input.split("\\s+");
                    if (ep.length < 3) {
                        p.sendMessage("§c형식: <효과명> <지속(초)> <레벨>");
                        return false;
                    }
                    ag.getActionBuilder().addPotionEffect(ep[0], Integer.parseInt(ep[1]), Integer.parseInt(ep[2]));
                    break;
                }
                case REMOVE_POTION_EFFECT:
                    ag.getActionBuilder().removePotionEffect(input);
                    break;
                case GIVE_ITEM: {
                    String[] ip = input.split("\\s+");
                    if (ip.length < 2) {
                        p.sendMessage("§c형식: <아이템> <개수>");
                        return false;
                    }
                    ag.getActionBuilder().giveItem(ip[0], Integer.parseInt(ip[1]));
                    break;
                }
                case TAKE_ITEM: {
                    String[] ip = input.split("\\s+");
                    if (ip.length < 2) {
                        p.sendMessage("§c형식: <아이템> <개수>");
                        return false;
                    }
                    ag.getActionBuilder().takeItem(ip[0], Integer.parseInt(ip[1]));
                    break;
                }
                case SET_VARIABLE: {
                    String[] vp = input.split("\\s+", 2);
                    if (vp.length < 2) {
                        p.sendMessage("§c형식: <변수명> <값>");
                        return false;
                    }
                    ag.getActionBuilder().setVariable(vp[0], vp[1]);
                    break;
                }
                case ADD_VARIABLE: {
                    String[] vp = input.split("\\s+");
                    if (vp.length < 2) {
                        p.sendMessage("§c형식: <변수명> <숫자>");
                        return false;
                    }
                    ag.getActionBuilder().addVariable(vp[0], Double.parseDouble(vp[1]));
                    break;
                }
                case RANDOM_NUMBER: {
                    String[] rp = input.split("\\s+");
                    if (rp.length < 3) {
                        p.sendMessage("§c형식: <변수명> <최소> <최대>");
                        return false;
                    }
                    ag.getActionBuilder().randomNumber(rp[0], Integer.parseInt(rp[1]), Integer.parseInt(rp[2]));
                    break;
                }
                case IF_HAS_PERMISSION:
                    ag.getActionBuilder().ifHasPermission(input);
                    break;
                case IF_NOT_PERMISSION:
                    ag.getActionBuilder().ifNotPermission(input);
                    break;
                case IF_VARIABLE_EQUALS: {
                    String[] vp = input.split("\\s+", 2);
                    if (vp.length < 2) {
                        p.sendMessage("§c형식: <변수명> <값>");
                        return false;
                    }
                    ag.getActionBuilder().ifVariableEquals(vp[0], vp[1]);
                    break;
                }
                case IF_VARIABLE_NOT_EQUALS: {
                    String[] vp = input.split("\\s+", 2);
                    if (vp.length < 2) {
                        p.sendMessage("§c형식: <변수명> <값>");
                        return false;
                    }
                    ag.getActionBuilder().ifVariableNotEquals(vp[0], vp[1]);
                    break;
                }
                case IF_VARIABLE_GREATER: {
                    String[] vp = input.split("\\s+");
                    if (vp.length < 2) {
                        p.sendMessage("§c형식: <변수명> <숫자>");
                        return false;
                    }
                    ag.getActionBuilder().ifVariableGreater(vp[0], Double.parseDouble(vp[1]));
                    break;
                }
                case IF_VARIABLE_LESS: {
                    String[] vp = input.split("\\s+");
                    if (vp.length < 2) {
                        p.sendMessage("§c형식: <변수명> <숫자>");
                        return false;
                    }
                    ag.getActionBuilder().ifVariableLess(vp[0], Double.parseDouble(vp[1]));
                    break;
                }
                case CALL_ACTION:
                    ag.getActionBuilder().callAction(input);
                    break;
                default:
                    p.sendMessage("§c지원하지 않는 입력 형식입니다.");
                    return false;
            }
            return true;
        } catch (NumberFormatException ex) {
            p.sendMessage("§c숫자 형식이 올바르지 않습니다: " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            p.sendMessage("§c오류: " + ex.getMessage());
            return false;
        }
    }

    private void sendInputPrompt(Player p, ActionType type) {
        p.sendMessage("§e[ActionBuilder] §a입력 모드 §7- §c취소: 'cancel'");
        switch (type) {
            case DELAY:
                p.sendMessage("§7딜레이 틱 수를 입력하세요. §8(예: §f20§8)");
                break;
            case SEND_MESSAGE:
                p.sendMessage("§7전송할 메시지를 입력하세요. §8(색상 코드 &, 플레이스홀더 {player} 사용 가능)");
                break;
            case SEND_TITLE:
                p.sendMessage("§7제목을 입력하세요. §8(형식: §f제목|부제목|fadein|stay|fadeout§8)");
                break;
            case SEND_ACTIONBAR:
                p.sendMessage("§7액션바 메시지를 입력하세요.");
                break;
            case BROADCAST:
            case BROADCAST_WORLD:
                p.sendMessage("§7방송할 메시지를 입력하세요.");
                break;
            case EXECUTE_AS_ADMIN:
                p.sendMessage("§7실행할 명령어를 입력하세요. §8(슬래시 제외)");
                break;
            case EXECUTE_AS_PLAYER:
                p.sendMessage("§7플레이어로 실행할 명령어를 입력하세요. §8(슬래시 제외)");
                break;
            case TELEPORT:
                p.sendMessage("§7텔레포트 위치를 입력하세요. §8(형식: §f월드명 x,y,z§8)");
                break;
            case SET_GAMEMODE:
                p.sendMessage("§7게임모드를 입력하세요. §8(SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR)");
                break;
            case GIVE_EXP:
                p.sendMessage("§7지급할 경험치량을 입력하세요.");
                break;
            case TAKE_EXP:
                p.sendMessage("§7제거할 경험치량을 입력하세요.");
                break;
            case SET_HEALTH:
                p.sendMessage("§7설정할 체력을 입력하세요. §8(0~20)");
                break;
            case SET_HUNGER:
                p.sendMessage("§7설정할 배고픔 수치를 입력하세요. §8(0~20)");
                break;
            case KICK:
                p.sendMessage("§7킥 사유를 입력하세요.");
                break;
            case PLAY_SOUND:
                p.sendMessage("§7사운드 정보를 입력하세요. §8(형식: §f사운드명 [볼륨] [피치]§8)");
                break;
            case PLAY_PARTICLE:
                p.sendMessage("§7파티클 정보를 입력하세요. §8(형식: §f파티클명 개수§8)");
                break;
            case ADD_POTION_EFFECT:
                p.sendMessage("§7포션 효과를 입력하세요. §8(형식: §f효과명 지속시간(초) 레벨§8)");
                break;
            case REMOVE_POTION_EFFECT:
                p.sendMessage("§7제거할 포션 효과명을 입력하세요.");
                break;
            case GIVE_ITEM:
                p.sendMessage("§7지급할 아이템 정보를 입력하세요. §8(형식: §f아이템명 개수§8)");
                break;
            case TAKE_ITEM:
                p.sendMessage("§7제거할 아이템 정보를 입력하세요. §8(형식: §f아이템명 개수§8)");
                break;
            case SET_VARIABLE:
                p.sendMessage("§7변수를 설정하세요. §8(형식: §f변수명 값§8)");
                break;
            case ADD_VARIABLE:
                p.sendMessage("§7변수에 더할 값을 입력하세요. §8(형식: §f변수명 숫자§8)");
                break;
            case RANDOM_NUMBER:
                p.sendMessage("§7랜덤 숫자 범위를 입력하세요. §8(형식: §f변수명 최소 최대§8)");
                break;
            case IF_HAS_PERMISSION:
                p.sendMessage("§7확인할 권한 노드를 입력하세요.");
                break;
            case IF_NOT_PERMISSION:
                p.sendMessage("§7확인할 권한 노드를 입력하세요. §8(권한이 없을 때 실행)");
                break;
            case IF_VARIABLE_EQUALS:
                p.sendMessage("§7비교할 변수와 값을 입력하세요. §8(형식: §f변수명 값§8)");
                break;
            case IF_VARIABLE_NOT_EQUALS:
                p.sendMessage("§7비교할 변수와 값을 입력하세요. §8(형식: §f변수명 값§8, 다를 때 실행)");
                break;
            case IF_VARIABLE_GREATER:
                p.sendMessage("§7비교할 변수와 기준값을 입력하세요. §8(형식: §f변수명 숫자§8, 클 때 실행)");
                break;
            case IF_VARIABLE_LESS:
                p.sendMessage("§7비교할 변수와 기준값을 입력하세요. §8(형식: §f변수명 숫자§8, 작을 때 실행)");
                break;
            case CALL_ACTION:
                p.sendMessage("§7호출할 액션 이름을 입력하세요.");
                break;
            default:
                p.sendMessage("§7정보를 입력하세요.");
        }
    }

    private boolean isNoParamAction(ActionType type) {
        return type == ActionType.CLOSE_INVENTORY
                || type == ActionType.CLEAR_EFFECTS
                || type == ActionType.ELSE
                || type == ActionType.END_IF
                || type == ActionType.CANCEL;
    }

    private void addNoParamAction(ActionGUI ag, ActionType type) {
        switch (type) {
            case CLOSE_INVENTORY: ag.getActionBuilder().closeInventory(); break;
            case CLEAR_EFFECTS: ag.getActionBuilder().clearEffects(); break;
            case ELSE: ag.getActionBuilder().orElse(); break;
            case END_IF: ag.getActionBuilder().endIf(); break;
            case CANCEL: ag.getActionBuilder().cancel(); break;
        }
    }
}
