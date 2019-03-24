#!/usr/bin/awk -f

# Count number of files: increment on the first line of each new file
BEGIN {
    OFS=","
}

FNR == 1  {++nfiles }

{
    for (i = 1; i <= NF; ++i)
        values[FNR, i] += $i
}

END {
    for (i = 1; i <= FNR; ++i)
    {
        for (j = 1; j <= NF; ++j)
        {
            $j = values[i,j]/nfiles
        }
        print
    }
}
