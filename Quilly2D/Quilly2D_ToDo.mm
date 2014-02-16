<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1392538545247" ID="ID_738665233" MODIFIED="1392538628415" TEXT="Quilly2D">
<node CREATED="1392538638292" ID="ID_1326741259" MODIFIED="1392538644340" POSITION="right" TEXT="Q2DTexture">
<node CREATED="1392538645638" ID="ID_378288882" MODIFIED="1392568465362" TEXT="replaces BufferedImage for user">
<icon BUILTIN="button_ok"/>
</node>
<node CREATED="1392538657447" ID="ID_553339968" MODIFIED="1392568462407" TEXT="supports alphachannel setting">
<icon BUILTIN="button_ok"/>
</node>
<node CREATED="1392538670159" ID="ID_1059798196" MODIFIED="1392568469592" TEXT="automatically optimizes the bufferedimage">
<icon BUILTIN="button_ok"/>
</node>
<node CREATED="1392538679228" ID="ID_509128477" MODIFIED="1392538691674" TEXT="allow creation of &quot;empty&quot; texture to paint on it">
<node CREATED="1392538692483" ID="ID_1496076397" MODIFIED="1392538703294" TEXT="f.e. each layer of the editor world can be one texture"/>
</node>
<node CREATED="1392538705646" ID="ID_1865241027" MODIFIED="1392538720641" TEXT="optimize rendering that only a portion of the texture is rendered">
<node CREATED="1392538721316" ID="ID_1261890099" MODIFIED="1392538750281" TEXT="f.e. if we have a 10000x10000 texture but the camera can only show 800x600 pixel then only render that section and not the entire texture"/>
</node>
</node>
<node CREATED="1392538807972" ID="ID_1450470459" MODIFIED="1392538820666" POSITION="left" TEXT="General optimization">
<node CREATED="1392538823235" ID="ID_495711968" MODIFIED="1392538843288" TEXT="Q2DEditor mappanel should use the Q2DFramework for rendering">
<node CREATED="1392539041599" ID="ID_900856777" MODIFIED="1392539054695" TEXT="try to integrate Q2DMainPanel to the Q2DEditorMapPanel"/>
</node>
<node CREATED="1392538846402" ID="ID_1282478077" MODIFIED="1392538871830" TEXT="Animated Sprites should no longer be animated when the &quot;testapplication&quot; is started"/>
<node CREATED="1392538875107" ID="ID_1864335427" MODIFIED="1392538895981" TEXT="Sond &quot;Thread&quot; of the FXPanel should be &quot;disposed&quot; when the testapplication is closed"/>
<node CREATED="1392538973958" ID="ID_489916454" MODIFIED="1392538992677" TEXT="All non-animated sprites should be rendered on a single Q2DTexture">
<node CREATED="1392538993351" ID="ID_610379371" MODIFIED="1392538997166" TEXT="one texture per layer"/>
</node>
<node CREATED="1392539070189" ID="ID_594548472" MODIFIED="1392539081363" TEXT="java.util.map loops">
<node CREATED="1392539082458" ID="ID_1774725428" MODIFIED="1392539098216" TEXT="iterate over values or keys or Map.entry"/>
<node CREATED="1392539101011" ID="ID_863948664" MODIFIED="1392539114816" TEXT="do not iterate over keys and then use map.get(key) inside the loop"/>
</node>
<node CREATED="1392539811228" ID="ID_1644767410" MODIFIED="1392539818569" TEXT="editor collision">
<node CREATED="1392539820271" ID="ID_1452583072" MODIFIED="1392539830178" TEXT="create collision regions for each layer">
<node CREATED="1392539836744" ID="ID_256672785" MODIFIED="1392539854615" TEXT="a region contains all &quot;tile regions&quot; that have collision"/>
</node>
<node CREATED="1392539832511" ID="ID_8937095" MODIFIED="1392539938203" TEXT="add a new method &quot;hasCollision(x,y)&quot; to Q2DApplication">
<node CREATED="1392539967188" ID="ID_1167115852" MODIFIED="1392539977922" TEXT="returns true if x,y are inside a collision region"/>
<node CREATED="1392539978982" ID="ID_292674398" MODIFIED="1392539982057" TEXT="returns fale otherwise"/>
</node>
</node>
</node>
<node CREATED="1392539159401" ID="ID_497674879" MODIFIED="1392539163777" POSITION="right" TEXT="Q2DEditor">
<node CREATED="1392539281256" ID="ID_654244021" MODIFIED="1392564805704" TEXT="support events">
<node CREATED="1392539419392" ID="ID_1757736884" MODIFIED="1392539422727" TEXT="general map events">
<node CREATED="1392539288037" ID="ID_682320782" MODIFIED="1392539294247" TEXT="onLoad"/>
<node CREATED="1392539295905" ID="ID_503028985" MODIFIED="1392539307509" TEXT="onExit"/>
</node>
<node CREATED="1392539433791" ID="ID_395452262" MODIFIED="1392539496874" TEXT="specific region events">
<node CREATED="1392539323157" ID="ID_988923066" MODIFIED="1392539346185" TEXT="onEnter">
<node CREATED="1392539348146" ID="ID_1092349017" MODIFIED="1392539358078" TEXT="used when entering a specific tile"/>
</node>
<node CREATED="1392539479981" ID="ID_86947768" MODIFIED="1392539482964" TEXT="onExit"/>
<node CREATED="1392539444336" ID="ID_262829899" MODIFIED="1392539448379" TEXT="onKeyPressed">
<node CREATED="1392539631140" ID="ID_1630736120" MODIFIED="1392539643532" TEXT="can be used for shops/doors/etc."/>
</node>
</node>
<node CREATED="1392539683005" ID="ID_996888432" MODIFIED="1392539690224" TEXT="specific entity events">
<node CREATED="1392539691595" ID="ID_483532675" MODIFIED="1392539694843" TEXT="onCollision"/>
<node CREATED="1392539696640" ID="ID_580035367" MODIFIED="1392539699914" TEXT="onKeyPressed"/>
<node CREATED="1392539721633" ID="ID_526274707" MODIFIED="1392539734828" TEXT="onEnter">
<node CREATED="1392539735397" ID="ID_794687743" MODIFIED="1392539747719" TEXT="can only be used when a range for the entity is specified"/>
<node CREATED="1392539765152" ID="ID_383747528" MODIFIED="1392539776301" TEXT="f.e. if you are not allowed to come close to a specific NPC"/>
</node>
</node>
</node>
<node CREATED="1392539164378" ID="ID_1120777898" MODIFIED="1392539172108" TEXT="support lightmaps">
<node CREATED="1392539173672" ID="ID_712449496" MODIFIED="1392539239378" TEXT="ambient color for entire map"/>
<node CREATED="1392539177663" ID="ID_1110902789" MODIFIED="1392539184358" TEXT="light sources">
<node CREATED="1392539185099" ID="ID_1108792088" MODIFIED="1392539191593" TEXT="min/max radius"/>
<node CREATED="1392539192842" ID="ID_1871877502" MODIFIED="1392539194106" TEXT="color"/>
<node CREATED="1392539196323" ID="ID_1748175976" MODIFIED="1392539229015" TEXT="time interval between min/max radius"/>
<node CREATED="1392539246728" ID="ID_1379118400" MODIFIED="1392539259725" TEXT="center coordinates"/>
</node>
</node>
</node>
</node>
</map>
