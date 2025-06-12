import { MdRemoveRedEye } from "react-icons/md";
import Tooltip from "@mui/material/Tooltip";
import { IconButton } from "@mui/material";
import { truncateText } from "../../utils/truncateText"; //sets the limit of how many characters will appear on the Note Icon for each note.
import { Link } from "react-router-dom";
import "react-quill/dist/quill.snow.css";
import moment from "moment";

const NoteItems = ({ parsedContent, id, createdAt }) => {
  const formattedDate = moment(createdAt).format("DD MMMM, YYYY");
  return (
    <div className="sm:px-5 px-2 py-5 shadow-md bg-noteColor shadow-red-400 rounded-lg min-h-96 max-h-96 relative overflow-hidden transition-transform transform hover:scale-[1.02] ">
      <p
        className="text-black font-customWeight ql-editor"
        dangerouslySetInnerHTML={{ __html: truncateText(parsedContent) }}
      ></p>
      <div className="flex justify-between items-center  absolute bottom-5 sm:px-5 px-2 left-0 w-full text-slate-700">
        <span className="font-medium">{formattedDate}</span>
        <Link to={`/notes/${id}`}>
          {" "}
          <Tooltip title="View Note">
            <IconButton>
              <MdRemoveRedEye className="text-slate-700 font-semibold" />
            </IconButton>
          </Tooltip>
        </Link>
      </div>
    </div>
  );
};

export default NoteItems;
