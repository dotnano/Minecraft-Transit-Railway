package mtr.packet;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import mtr.data.AreaBase;
import mtr.data.IGui;
import mtr.data.RailwayData;
import net.minecraft.world.level.Level;

import java.util.Set;

public class UpdateBlueMap implements IGui, IUpdateWebMap {

	public static void updateBlueMap(Level world, RailwayData railwayData) {
		try {
			updateBlueMap(world, railwayData.stations, MARKER_SET_STATIONS_ID, MARKER_SET_STATIONS_TITLE, MARKER_SET_STATION_AREAS_ID, MARKER_SET_STATION_AREAS_TITLE, STATION_ICON_PATH);
			updateBlueMap(world, railwayData.depots, MARKER_SET_DEPOTS_ID, MARKER_SET_DEPOTS_TITLE, MARKER_SET_DEPOT_AREAS_ID, MARKER_SET_DEPOT_AREAS_TITLE, DEPOT_ICON_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static <T extends AreaBase> void updateBlueMap(Level world, Set<T> areas, String areasId, String areasTitle, String areaAreasId, String areaAreasTitle, String iconKey) {
		final BlueMapAPI api = BlueMapAPI.getInstance().orElse(null);
		if (api == null) {
			return;
		}

		final String worldId = world.dimension().location().getPath();
		final BlueMapMap map = api.getMaps().stream().filter(map1 -> worldId.contains(map1.getId())).findFirst().orElse(null);
		if (map == null) {
			return;
		}

		final int areaY = world.getSeaLevel();

		final MarkerSet markerSetAreas = MarkerSet.builder().label(areasTitle).build();
		markerSetAreas.getMarkers().clear();
		map.getMarkerSets().put(areasId, markerSetAreas);

		final MarkerSet markerSetAreaAreas = MarkerSet.builder().label(areaAreasTitle).defaultHidden(true).build();
		markerSetAreaAreas.getMarkers().clear();
		map.getMarkerSets().put(areaAreasId, markerSetAreaAreas);

		IUpdateWebMap.iterateAreas(areas, (id, name, color, areaCorner1X, areaCorner1Z, areaCorner2X, areaCorner2Z, areaX, areaZ) -> {
			final POIMarker markerArea = POIMarker.builder()
					.position((double) areaX, (double) areaY, (double) areaZ)
					.label(name)
					.icon(iconKey, ICON_SIZE / 2, ICON_SIZE / 2)
					.build();
			markerSetAreas.getMarkers().put("1_" + worldId + id, markerArea);
			final ExtrudeMarker markerAreaArea = ExtrudeMarker.builder()
					.position((double) areaX, (double) areaY, (double) areaZ)
					.shape(Shape.createRect((double) areaCorner1X, (double) areaCorner1Z, (double) areaCorner2X, (double) areaCorner2Z), -64, 319)
					.label(name)
					.fillColor(new Color(color.getRGB() & RGB_WHITE, 0.5F))
					.lineColor(new Color(color.darker().getRGB()))
					.build();
			markerSetAreas.getMarkers().put("2_" + worldId + id, markerAreaArea);
		});
	}
}
