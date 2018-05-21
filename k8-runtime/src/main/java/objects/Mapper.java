package objects;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;


public class Mapper {

	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * 
	 * @param current
	 * @param desired
	 * @return
	 */
	public static Map[] determineJsonPatch(Object current, Object desired) {

		JsonNode desiredNode = mapper.convertValue(desired, JsonNode.class);
		JsonNode currentNode = mapper.convertValue(current, JsonNode.class);

		return mapper.convertValue(JsonDiff.asJson(currentNode, desiredNode), Map[].class);
	}

}
