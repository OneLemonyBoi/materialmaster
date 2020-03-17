var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
var InvokeDynamicInsnNode = Java.type('org.objectweb.asm.tree.InvokeDynamicInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var FrameNode = Java.type('org.objectweb.asm.tree.FrameNode');

function initializeCoreMod() {

    return {

        // apply custom attributes for every item
        'item_stack_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.ItemStack'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "func_111283_C",
                    name: "getAttributeModifiers",
                    desc: "(Lnet/minecraft/inventory/EquipmentSlotType;)Lcom/google/common/collect/Multimap;",
                    patches: [patchItemStackGetAttributeModifiers]
                }], classNode, "ItemStack");
                return classNode;
            }
        },

        // extended attack reach
        'game_renderer_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.renderer.GameRenderer'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "func_78473_a",
                    name: "getMouseOver",
                    desc: "(F)V",
                    patches: [patchGameRendererGetMouseOver1, patchGameRendererGetMouseOver2]
                }], classNode, "GameRenderer");
                return classNode;
            }
        },

        // extended attack reach server side
        'server_play_net_handler_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.network.play.ServerPlayNetHandler'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "func_147340_a",
                    name: "processUseEntity",
                    desc: "(Lnet/minecraft/network/play/client/CUseEntityPacket;)V",
                    patches: [patchServerPlayNetHandlerProcessUseEntity]
                }], classNode, "ServerPlayNetHandler");
                return classNode;
            }
        },

        // enable generic.attackKnockback attribute for players
        'player_entity_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.entity.player.PlayerEntity'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "func_71059_n",
                    name: "attackTargetEntityWithCurrentItem",
                    desc: "(Lnet/minecraft/entity/Entity;)V",
                    patches: [patchPlayerEntityAttackTargetEntityWithCurrentItem]
                }], classNode, "PlayerEntity");
                return classNode;
            }
        },

        // make it possible to change item enchantability
        'enchantment_helper_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.enchantment.EnchantmentHelper'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "func_77514_a",
                    name: "calcItemStackEnchantability",
                    desc: "(Ljava/util/Random;IILnet/minecraft/item/ItemStack;)I",
                    patches: [patchEnchantmentHelperCalcItemStackEnchantability]
                }, {
                    obfName: "func_77513_b",
                    name: "buildEnchantmentList",
                    desc: "(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Ljava/util/List;",
                    patches: [patchEnchantmentHelperBuildEnchantmentList]
                }], classNode, "EnchantmentHelper");
                return classNode;
            }
        },

        // make it possible to change tool harvest level
        'item_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.Item'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "getHarvestLevel",
                    name: "getHarvestLevel",
                    desc: "(Lnet/minecraft/item/ItemStack;Lnet/minecraftforge/common/ToolType;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/block/BlockState;)I",
                    patches: [patchItemGetHarvestLevel]
                }], classNode, "Item");
                return classNode;
            }
        }
    };
}

function patchMethod(entries, classNode, name) {

    log("Patching " + name + "...");
    for (var i = 0; i < entries.length; i++) {

        var entry = entries[i];
        var method = findMethod(classNode.methods, entry);
        var flag = !!method;
        if (flag) {

            var obfuscated = !method.name.equals(entry.name);
            for (var j = 0; j < entry.patches.length; j++) {

                var patch = entry.patches[j];
                if (!patchInstructions(method, patch.filter, patch.action, obfuscated)) {

                    flag = false;
                }
            }
        }

        log("Patching " + name + "#" + entry.name + (flag ? " was successful" : " failed"));
    }
}

function findMethod(methods, entry) {

    for (var i = 0; i < methods.length; i++) {

        var method = methods[i];
        if ((method.name.equals(entry.obfName) || method.name.equals(entry.name)) && method.desc.equals(entry.desc)) {

            return method;
        }
    }
}

function patchInstructions(method, filter, action, obfuscated) {

    var instructions = method.instructions.toArray();
    for (var i = 0; i < instructions.length; i++) {

        var node = filter(instructions[i], obfuscated);
        if (!!node) {

            break;
        }
    }

    if (!!node) {

        action(node, method.instructions, obfuscated);
        return true;
    }
}

var patchItemGetHarvestLevel = {
    filter: function(node, obfuscated) {
        if (node instanceof InsnNode && node.getOpcode().equals(Opcodes.IRETURN)) {
            return node.getPrevious();
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(generateHook("getHarvestLevel", "(ILnet/minecraft/item/Item;)I"));
        instructions.insert(node, insnList);
    }
};

var patchEnchantmentHelperBuildEnchantmentList = {
    filter: function(node, obfuscated) {
        // getItemEnchantability is a Forge method
        if (matchesMethod(node, "net/minecraft/item/ItemStack", obfuscated ? "getItemEnchantability" : "getItemEnchantability", "()I")) {
            var nextNode = node.getNext();
            if (nextNode instanceof VarInsnNode && nextNode.getOpcode().equals(Opcodes.ISTORE) && nextNode.var.equals(6)) {
                return node;
            }
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(generateHook("getItemEnchantability", "(ILnet/minecraft/item/ItemStack;)I"));
        instructions.insert(node, insnList);
    }
};

var patchEnchantmentHelperCalcItemStackEnchantability = {
    filter: function(node, obfuscated) {
        // getItemEnchantability is a Forge method
        if (matchesMethod(node, "net/minecraft/item/ItemStack", obfuscated ? "getItemEnchantability" : "getItemEnchantability", "()I")) {
            var nextNode = node.getNext();
            if (nextNode instanceof VarInsnNode && nextNode.getOpcode().equals(Opcodes.ISTORE) && nextNode.var.equals(5)) {
                return node;
            }
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 3));
        insnList.add(generateHook("getItemEnchantability", "(ILnet/minecraft/item/ItemStack;)I"));
        instructions.insert(node, insnList);
    }
};

var patchPlayerEntityAttackTargetEntityWithCurrentItem = {
    filter: function(node, obfuscated) {
        if (node instanceof InsnNode && node.getOpcode().equals(Opcodes.ICONST_0)) {
            var nextNode = node.getNext();
            if (nextNode instanceof VarInsnNode && nextNode.getOpcode().equals(Opcodes.ISTORE) && nextNode.var.equals(7)) {
                nextNode = getNthNode(node, 6);
                if (matchesMethod(nextNode, "net/minecraft/enchantment/EnchantmentHelper", obfuscated ? "func_77501_a" : "getKnockbackModifier", "(Lnet/minecraft/entity/LivingEntity;)I")) {
                    return node;
                }
            }
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(new InsnNode(Opcodes.POP));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(generateHook("getKnockbackAttribute", "(Lnet/minecraft/entity/player/PlayerEntity;)I"));
        instructions.insert(node, insnList);
    }
};

var patchServerPlayNetHandlerProcessUseEntity = {
    filter: function(node, obfuscated) {
        if (node instanceof VarInsnNode && node.getOpcode().equals(Opcodes.DLOAD) && node.var.equals(5)) {
            var nextNode = node.getPrevious();
            if (matchesMethod(nextNode, "net/minecraft/entity/player/ServerPlayerEntity", obfuscated ? "func_70068_e" : "getDistanceSq", "(Lnet/minecraft/entity/Entity;)D")) {
                return node;
            }
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(new InsnNode(Opcodes.POP2));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/play/ServerPlayNetHandler", obfuscated ? "field_147369_b" : "player", "Lnet/minecraft/entity/player/ServerPlayerEntity;"));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 3));
        insnList.add(generateHook("getEntityReachDistance", "(Lnet/minecraft/entity/player/ServerPlayerEntity;Lnet/minecraft/entity/Entity;)D"));
        instructions.insert(node, insnList);
    }
};

var patchGameRendererGetMouseOver1 = {
    filter: function(node, obfuscated) {
        if (node instanceof LdcInsnNode) {
            var nextNode = node.getNext();
            if (nextNode instanceof InsnNode && nextNode.getOpcode().equals(Opcodes.DCMPL)) {
                nextNode = node.getPrevious();
                if (nextNode instanceof VarInsnNode && nextNode.getOpcode().equals(Opcodes.DLOAD) && nextNode.var.equals(17)) {
                    return node;
                }
            }
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(new InsnNode(Opcodes.POP2));
        insnList.add(new VarInsnNode(Opcodes.DLOAD, 3));
        insnList.add(generateHook("getMaxSquareRange", "(D)D"));
        instructions.insert(node, insnList);
    }
};

var patchGameRendererGetMouseOver2 = {
    filter: function(node, obfuscated) {
        if (node instanceof VarInsnNode && node.getOpcode().equals(Opcodes.ALOAD) && node.var.equals(2)) {
            var nextNode = node.getNext();
            if (nextNode instanceof InsnNode && nextNode.getOpcode().equals(Opcodes.FCONST_1)) {
                nextNode = nextNode.getNext();
                if (matchesMethod(nextNode, "net/minecraft/entity/Entity", obfuscated ? "func_70676_i" : "getLook", "(F)Lnet/minecraft/util/math/Vec3d;")) {
                    return node;
                }
            }
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(generateHook("getAttackReachDistance", "()D"));
        insnList.add(new VarInsnNode(Opcodes.DSTORE, 3));
        insnList.add(new VarInsnNode(Opcodes.FLOAD, 1));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 2));
        insnList.add(generateHook("getSquareAttackDistance", "(FLnet/minecraft/entity/Entity;)D"));
        insnList.add(new VarInsnNode(Opcodes.DSTORE, 8));
        instructions.insert(node, insnList);
    }
};

var patchItemStackGetAttributeModifiers = {
    filter: function(node, obfuscated) {
        // getAttributeModifiers is a Forge method
        if (matchesMethod(node, "net/minecraft/item/Item", obfuscated ? "getAttributeModifiers" : "getAttributeModifiers", "(Lnet/minecraft/inventory/EquipmentSlotType;Lnet/minecraft/item/ItemStack;)Lcom/google/common/collect/Multimap;")) {
            var nextNode = node.getNext();
            if (nextNode instanceof VarInsnNode && nextNode.getOpcode().equals(Opcodes.ASTORE)) {
                return node;
            }
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(generateHook("adjustAttributeMap", "(Lcom/google/common/collect/Multimap;Lnet/minecraft/inventory/EquipmentSlotType;Lnet/minecraft/item/ItemStack;)Lcom/google/common/collect/Multimap;"));
        instructions.insert(node, insnList);
    }
};

function matchesMethod(node, owner, name, desc) {

    return node instanceof MethodInsnNode && matchesNode(node, owner, name, desc);
}

function matchesField(node, owner, name, desc) {

    return node instanceof FieldInsnNode && matchesNode(node, owner, name, desc);
}

function matchesNode(node, owner, name, desc) {

    return node.owner.equals(owner) && node.name.equals(name) && node.desc.equals(desc);
}

function generateHook(name, desc) {

    return new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/materialmaster/asm/Hooks", name, desc, false);
}

function getNthNode(node, n) {

    for (var i = 0; i < Math.abs(n); i++) {

        if (n < 0) {

            node = node.getPrevious();
        } else {

            node = node.getNext();
        }
    }

    return node;
}

function log(message) {

    print("[Material Master Transformer]: " + message);
}