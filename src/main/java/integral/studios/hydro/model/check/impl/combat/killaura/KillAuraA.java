package integral.studios.hydro.model.check.impl.combat.killaura;

//public class KillAuraA extends PacketCheck {
//    public KillAuraA(PlayerData playerData) {
//        super(playerData, "Kill Aura A", "Placing or Digging Whilst Attacking Check", new ViolationHandler(1, 300L), Category.COMBAT, SubCategory.KILL_AURA);
//    }
//
//    @Override
//    public void handle(PacketReceiveEvent event) {
//        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
//            WrapperPlayClientInteractEntity useEntity = new WrapperPlayClientInteractEntity(event);
//
//            // This falses because we have to retrace for digging not a big deal at the moment
//            if (useEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
//                if (/*actionTracker.isPlacing() ||*/ actionTracker.isDigging()) {
//                    handleViolation(new PlayerViolation(this));
//                }
//            }
//        }
//    }
//}