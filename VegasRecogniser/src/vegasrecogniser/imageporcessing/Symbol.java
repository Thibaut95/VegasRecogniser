
package vegasrecogniser.imageporcessing;

public enum Symbol
	{

	CLUB(Constant.TABSYMBOL[0]),
	DIAMOND(Constant.TABSYMBOL[1]),
	HEART(Constant.TABSYMBOL[2]),
	SPADE(Constant.TABSYMBOL[3]);

	private final String name;

	private Symbol(String name)
		{
		this.name = name;
		}

	public String getName()
		{
		return name;
		}

	}
