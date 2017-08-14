
	<div class="copyspace">
            
	    <h3>Lizard Enclosure - Confirm the New Schedule</h3>           

            <div class="featuredProject">
                <center>
                    <form method="POST" action="${pageContext.request.contextPath}/schedule/save">                    
                        new cron schedule:
                        <br/>
                        <input type="hidden" name="newSchedlue" value="${newSchedlue}"> 
<pre>                    
${newSchedlue}
</pre>              



                        </b>
                        old cron schedule:
                        <br/>
<pre>
${cronTable}
</pre>

                        <input type="submit" value="Save">
                    </form>
                </center>
            
	    </div>
            
	</div>
