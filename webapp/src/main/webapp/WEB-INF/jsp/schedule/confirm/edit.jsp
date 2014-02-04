
	<div class="copyspace">
            
	    <h3>Lizard Enclosure - Confirm the New Schedule</h3>           

            <div class="featuredProject">
                <center>
                    new cron schedule:
                    <br/>
<pre>                    
${cronTable}
</pre>              
                    </b>
                    old cron schedule:
                    <br/>
<pre>                    
${cronTable}
</pre>              
                    <form method="POST" action="${pageContext.request.contextPath}/schedule/save">
                        <input type="submit" value="Save">
                    </form>
                </center>
            
	    </div>
            
	</div>
