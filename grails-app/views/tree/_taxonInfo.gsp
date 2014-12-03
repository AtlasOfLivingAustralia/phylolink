<div class="well-small">
    <table class="table table-condensed table-bordered">
        <tr>
            <td>Name</td><td>${taxon.name}</td>
        </tr>
        <tr>
            <td>Rank</td><td>${taxon.rawRank}</td>
        </tr>
        <tr>
            <td>Found in Australia?</td><td>${ taxon.isAustralian == 'recorded' ? 'Yes' : 'No' }</td>
        </tr>
        <tr>
            <td>Kingdom</td><td>${taxon.kingdom}</td>
        </tr>
        <tr>
            <td>Phylum</td><td>${taxon.phylum}</td>
        </tr>
        <tr>
            <td>Class</td><td>${taxon.classs}</td>
        </tr>
        <tr> 
            <td>Order</td><td>${taxon.order}</td>
        </tr>
        <tr>
            <td>Family</td><td>${taxon.family}</td>
        </tr>
        <tr>
            <td>Genus</td><td>${taxon.genus}</td>
        </tr>
    </table>
</div>