var Cloud = 
{
		onSearch: function(cb, $span, searchingText)
		{
			//TODO: spinner
			$span.text(searchingText);
			
			var updateCount = function(data)
			{
				if ($span[0] && document.contains($span[0]))
				{
					$span.text(data.text);
				}
			}
			cb.call(null, updateCount);
		}
};