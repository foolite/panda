package panda.mvc.testapp.classes.action.adaptor;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import panda.mvc.adaptor.meta.Pet;
import panda.mvc.annotation.At;
import panda.mvc.annotation.To;
import panda.mvc.annotation.param.Param;
import panda.mvc.testapp.BaseWebappTest;
import panda.mvc.view.Views;

@At("/adaptor")
@To(value=Views.RAW, fatal=Views.SC_INTERNAL_ERROR)
public class AdaptorTestModule extends BaseWebappTest {

	@At("edate")
	public String getDate(@Param(value="d", format="yyyyMMdd") Date d) throws IOException {
		return String.valueOf(d.getTime());
	}

	@At("json/pet/array")
	public String getJsonPetArray(@Param("pets") Pet[] pets) {
		return String.format("pets(%d) %s", pets.length, "array");
	}

	@At("json/pet/list")
	public String getJsonPetList(@Param("pets") List<Pet> lst) {
		StringBuilder sb = new StringBuilder();
		for (Pet pet : lst)
			sb.append(',').append(pet.getName());
		return String.format("pets(%d) %s", lst.size(), "list");
	}

	@At("json/type")
	public void jsonMapType(@Param Map<String, Double> map) {
		TestCase.assertNotNull(map);
		TestCase.assertEquals(1, map.size());
		TestCase.assertEquals(123456.0, map.get("abc").doubleValue());
		System.out.println(map.get("abc"));
	}
}
